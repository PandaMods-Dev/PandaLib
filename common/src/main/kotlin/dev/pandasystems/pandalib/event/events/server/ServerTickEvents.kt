package dev.pandasystems.pandalib.event.events.server

import dev.pandasystems.pandalib.core.utils.loadService
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.event
import net.minecraft.server.MinecraftServer

data class ServerTickEventContext(val server: MinecraftServer)

interface ServerTickEvents {
	val preServerTick: Event<ServerTickEventContext>
	val postServerTick: Event<ServerTickEventContext>

	companion object : ServerTickEvents by loadService()
}