package net.stellarica.server.crafts.starships.subsystems.computer

import net.stellarica.server.crafts.starships.Starship
import net.stellarica.server.crafts.starships.subsystems.Subsystem
import net.stellarica.server.lua.Lua
import kotlin.system.measureTimeMillis

class ComputerSubsystem(ship: Starship) : Subsystem(ship) {
	override fun onShipPiloted() {
		val t = measureTimeMillis {
			Lua.runScriptInSandbox(
				"""
				require("net.stellarica.server.lua.starships")
				
				while true do
					print("reeeeeee")
				end
				
				starships.shoot()
				
				print("Shot!")
			""".trimIndent()
			)
		}
		println("omegapoggers. finished in $t")
	}
}