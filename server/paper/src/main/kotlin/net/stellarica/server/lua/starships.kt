package net.stellarica.server.lua

import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.ZeroArgFunction

class starships: TwoArgFunction() {
	override fun call(modname: LuaValue?, env: LuaValue?): LuaValue {
		val library = tableOf()
		library["shoot"] = shoot()
		env!!.set( "starships", library );
		return library
	}


	class shoot: ZeroArgFunction() {
		override fun call(): LuaValue {
			return LuaValue.valueOf(true)
		}
	}
}