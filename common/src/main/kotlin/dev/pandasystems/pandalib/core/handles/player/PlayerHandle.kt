package dev.pandasystems.pandalib.core.handles.player

import dev.pandasystems.pandalib.core.MinecraftRuntime
import dev.pandasystems.pandalib.core.RuntimeEnvironment
import dev.pandasystems.pandalib.core.lifecycles.ClientLifecycle
import dev.pandasystems.pandalib.core.lifecycles.ServerLifecycle
import net.minecraft.world.entity.player.Player
import java.util.*

class PlayerHandle(
	val uuid: UUID
) {
	val isTracked: Boolean
		get() = resolve() != null

	val isOnline: Boolean
		get() = if (MinecraftRuntime.environment == RuntimeEnvironment.SERVER) {
			ServerLifecycle.serverInstance?.playerList?.getPlayer(uuid) != null
		} else {
			ClientLifecycle.clientInstance.connection?.getPlayerInfo(uuid) != null
		}

	fun resolve(): Player? = if (MinecraftRuntime.environment == RuntimeEnvironment.SERVER) {
		val server = ServerLifecycle.serverInstance
		server?.playerList?.getPlayer(uuid)
	} else {
		val level = ClientLifecycle.clientInstance.level
		level?.getPlayerByUUID(uuid)
	}
}

fun Player.handle(): PlayerHandle = PlayerHandle(uuid)