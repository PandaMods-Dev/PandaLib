package dev.pandasystems.pandalib.event.events.server

import dev.pandasystems.pandalib.core.utils.loadService
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.event
import net.minecraft.server.MinecraftServer

data class ServerTickEventContext(val server: MinecraftServer)

@Deprecated("Use ServerTickEvents.preServerTick")
val preServerTick by event<ServerTickEventContext>()
@Deprecated("Use ServerTickEvents.postServerTick")
val postServerTick by event<ServerTickEventContext>()

interface ServerTickEvents {
	val preServerTick: Event<ServerTickEventContext, Unit>
	val postServerTick: Event<ServerTickEventContext, Unit>

	companion object : ServerTickEvents by loadService()
}