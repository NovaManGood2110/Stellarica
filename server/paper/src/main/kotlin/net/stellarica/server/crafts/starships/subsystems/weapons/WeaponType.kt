package net.stellarica.server.crafts.starships.subsystems.weapons

import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.AcceleratingProjectile
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.InstantProjectile
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.LinearProjectile
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.Projectile
import net.stellarica.server.multiblocks.MultiblockType
import net.stellarica.server.multiblocks.Multiblocks
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Sound
import kotlin.math.PI

enum class WeaponType(
	val projectile: Projectile<*>,
	val direction: OriginRelative,
	val cone: Double,
	val mount: OriginRelative,
	val priority: Int,
	val multiblock: MultiblockType
) {
	LIGHT_RAILGUN(
		LinearProjectile(
			1f,
			Particle.FLAME,
			Sound.ITEM_TOTEM_USE,
			120,
			5.0
		),
		OriginRelative(8, 0, 0),
		PI / 6,
		OriginRelative(7, 0, 0),
		1,
		Multiblocks.LIGHT_RAILGUN
	),
	PLASMA_CANNON(
		AcceleratingProjectile(
			2f,
			Particle.SONIC_BOOM,
			Sound.BLOCK_CONDUIT_ACTIVATE,
			50,
			0.2,
			0.2
		),
		OriginRelative(8, 0, 0),
		PI / 8,
		OriginRelative(7, 0, 0),
		1,
		Multiblocks.PLASMA_CANNON
	),
	BATTLE_CANNON(
		LinearProjectile(
			2f,
			Particle.CAMPFIRE_COSY_SMOKE,
			Sound.ENTITY_GENERIC_EXPLODE,
			80,
			3.0
		),
		OriginRelative(9, 0, 0),
		PI / 6,
		OriginRelative(8, 0, 0),
		1,
		Multiblocks.BATTLE_CANNON
	),
	PULSE_LASER(
		InstantProjectile(
			2f,
			Particle.REDSTONE,
			Sound.ENTITY_BEE_HURT,
			120,
			Particle.DustOptions(Color.RED, 1f)
		),
		OriginRelative(5, 0, 0),
		PI / 7,
		OriginRelative(4, 0, 0),
		1,
		Multiblocks.PULSE_LASER
	);
}
