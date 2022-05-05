package io.github.hydrazinemc.hydrazine.starships

import io.github.hydrazinemc.hydrazine.utils.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.nms.setBlockFast
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional
import org.bukkit.scheduler.BukkitRunnable
import rotateBlockFace
import java.util.concurrent.ConcurrentHashMap

/**
 * Main bukkit runnable for setting starship blocks
 */
object StarshipBlockSetter : BukkitRunnable() {
	val blockSetQueueQueue =
		ConcurrentHashMap<
				MutableMap<BlockLocation, BlockData>,
				StarshipMoveData
				>()
	//Key is the blocks to set, value is the extra data for this move operation

	/**
	 * Should not be called manually, as this is part of a Bukkit runnable.
	 *
	 * Moves as many blockSetQueues from [blockSetQueueQueue] as it can in 40 ms.
	 * Can spend over 40ms running when moving large queues=
	 */
	override fun run() {
		val targetTime = System.currentTimeMillis() + 40
		while (System.currentTimeMillis() < targetTime) {
			if (blockSetQueueQueue.isEmpty()) break

			val moveData = blockSetQueueQueue.values.first()
			val blockSetQueue = blockSetQueueQueue.keys.first()

			moveData.ship.movePassengers(moveData.modifier, moveData.rotation)

			blockSetQueueQueue.remove(blockSetQueue)
			blockSetQueue!!.forEach {
				val data = it.value
				setBlockFast(it.key.asLocation, data)
				if (data is Directional) {
					data.facing = rotateBlockFace(data.facing, moveData.rotation)
				}
			}
			moveData.ship.isMoving = false
		}
	}
}