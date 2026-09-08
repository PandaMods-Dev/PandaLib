package dev.pandasystems.pandalib.event.events.server

import dev.pandasystems.pandalib.core.utils.loadService
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.event
import net.minecraft.server.MinecraftServer

data class ServerLifecycleEventContext(val server: MinecraftServer)

interface ServerLifecycleEvents {
	val starting: Event<ServerLifecycleEventContext>
	val started: Event<ServerLifecycleEventContext>
	val stopped: Event<ServerLifecycleEventContext>
	val stopping: Event<ServerLifecycleEventContext>

	companion object : ServerLifecycleEvents by loadService()
}
