package net.stellarica.server.lua

import org.luaj.vm2.Globals
import org.luaj.vm2.LoadState
import org.luaj.vm2.LuaString
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaThread
import org.luaj.vm2.LuaValue
import org.luaj.vm2.compiler.LuaC
import org.luaj.vm2.lib.Bit32Lib
import org.luaj.vm2.lib.DebugLib
import org.luaj.vm2.lib.PackageLib
import org.luaj.vm2.lib.TableLib
import org.luaj.vm2.lib.ZeroArgFunction
import org.luaj.vm2.lib.jse.JseBaseLib
import org.luaj.vm2.lib.jse.JseMathLib
import org.luaj.vm2.lib.jse.JseStringLib

object Lua {
	// These globals are used by the server to compile scripts.
	private var serverGlobals: Globals? = null
	init {
		// Create server globals with just enough library support to compile user scripts.
		serverGlobals = Globals()
		serverGlobals!!.load(JseBaseLib())
		serverGlobals!!.load(PackageLib())
		serverGlobals!!.load(JseStringLib())

		// To load scripts, we occasionally need a math library in addition to compiler support.
		// To limit scripts using the debug library, they must be closures, so we only install LuaC.
		serverGlobals!!.load(JseMathLib())
		LoadState.install(serverGlobals)
		LuaC.install(serverGlobals)

		// Set up the LuaString metatable to be read-only since it is shared across all scripts.
		LuaString.s_metatable = ReadOnlyLuaTable(LuaString.s_metatable)
	}


	// Run a script in a lua thread and limit it to a certain number
	// of instructions by setting a hook function.
	// Give each script its own copy of globals, but leave out libraries
	// that contain functions that can be abused.
	fun runScriptInSandbox(script: String) {

		// Each script will have its own set of globals, which should
		// prevent leakage between scripts running on the same server.
		val userGlobals = Globals()
		userGlobals.load(JseBaseLib())
		userGlobals.load(PackageLib())
		userGlobals.load(Bit32Lib())
		userGlobals.load(TableLib())
		userGlobals.load(JseStringLib())
		userGlobals.load(JseMathLib())


		// The debug library must be loaded for hook functions to work, which
		// allow us to limit scripts to run a certain number of instructions at a time.
		// However, we don't wish to expose the library in the user globals,
		// so it is immediately removed from the user globals once created.
		userGlobals.load(DebugLib())
		val sethook = userGlobals["debug"]["sethook"]
		userGlobals["debug"] = LuaValue.NIL

		// Set up the script to run in its own lua thread, which allows us
		// to set a hook function that limits the script to a specific number of cycles.
		// Note that the environment is set to the user globals, even though the
		// compiling is done with the server globals.
		val chunk = serverGlobals!!.load(script, "main", userGlobals)
		val thread = LuaThread(userGlobals, chunk)

		// Set the hook function to immediately throw an Error, which will not be
		// handled by any Lua code other than the coroutine.
		val hookfunc: LuaValue = object : ZeroArgFunction() {
			override fun call(): LuaValue {
				// A simple lua error may be caught by the script, but a
				// Java Error will pass through to top and stop the script.
				throw Error("Script overran resource limits.")
			}
		}
		val instructionCount = 200000

		sethook.invoke(
			LuaValue.varargsOf(
				arrayOf(
					thread, hookfunc,
					LuaValue.EMPTYSTRING, LuaValue.valueOf(instructionCount)
				)
			)
		)

		// When we resume the thread, it will run up to 'instruction_count' instructions
		// then call the hook function which will error out and stop the script.
		val result = thread.resume(LuaValue.NIL)
		println("[[$script]] -> $result")
	}

	// Simple read-only table whose contents are initialized from another table.
	internal class ReadOnlyLuaTable(table: LuaValue) : LuaTable() {
		init {
			presize(table.length(), 0)
			var n = table.next(NIL)
			while (!n.arg1().isnil()) {
				val key = n.arg1()
				val value = n.arg(2)
				super.rawset(key, if (value.istable()) ReadOnlyLuaTable(value) else value)
				n = table
					.next(n.arg1())
			}
		}

		override fun setmetatable(metatable: LuaValue): LuaValue {
			return error("table is read-only")
		}

		override fun set(key: Int, value: LuaValue) {
			error("table is read-only")
		}

		override fun rawset(key: Int, value: LuaValue) {
			error("table is read-only")
		}

		override fun rawset(key: LuaValue, value: LuaValue) {
			error("table is read-only")
		}

		override fun remove(pos: Int): LuaValue {
			return error("table is read-only")
		}
	}
}