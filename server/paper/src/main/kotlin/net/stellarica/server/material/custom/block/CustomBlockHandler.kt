package net.stellarica.server.material.custom.block

import net.minecraft.world.entity.item.ItemEntity
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.material.type.block.CustomBlockType
import net.stellarica.server.material.type.item.ItemType
import org.bukkit.craftbukkit.v1_19_R3.CraftServer
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftItem
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockPlaceEvent

object CustomBlockHandler : Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	fun onCustomBlockPlace(event: BlockPlaceEvent) {
		val type = ItemType.of(event.itemInHand)
		if (!type.isCustom || type.getBlock() == null) return

		event.blockPlaced.blockData = type.getBlock()!!.getBukkitBlockData()
		klogger.warn { event.blockPlaced.blockData }
	}

	@EventHandler
	fun onBlockDropItem(event: BlockDropItemEvent) {
		val type = BlockType.of(event.blockState)
		if (!type.isCustom) return

		type as CustomBlockType
		event.items.clear()

		val loc = event.block.location
		val drops = type.block.drops ?: type.block.item?.let { mapOf(ItemType.of(it) to 1) }

		drops?.forEach {
			val entity = ItemEntity(
				(loc.world as CraftWorld).handle,
				loc.x, loc.y, loc.z,
				CraftItemStack.asNMSCopy(it.key.getBukkitItemStack(it.value))
			)

			event.items.add(
				CraftItem(
					event.player.server as CraftServer,
					entity
				)
			)
		}
	}
}
