package dev.pandasystems.pandalib.neoforge.event.events.server

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.events.server.ServerLifecycleEventContext
import dev.pandasystems.pandalib.event.events.server.ServerLifecycleEvents
import dev.pandasystems.pandalib.neoforge.event.bindEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.server.ServerStoppedEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent

@AutoService(ServerLifecycleEvents::class)
class ServerLifecycleEventsImpl : ServerLifecycleEvents {
	override val starting: Event<ServerLifecycleEventContext> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerLifecycleEventContext(it.server) },
		convertFromCtx = { ServerStartingEvent(it.server) }
	)

	override val started: Event<ServerLifecycleEventContext> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerLifecycleEventContext(it.server) },
		convertFromCtx = { ServerStartedEvent(it.server) }
	)

	override val stopping: Event<ServerLifecycleEventContext> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerLifecycleEventContext(it.server) },
		convertFromCtx = { ServerStoppingEvent(it.server) }
	)

	override val stopped: Event<ServerLifecycleEventContext> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerLifecycleEventContext(it.server) },
		convertFromCtx = { ServerStoppedEvent(it.server) }
	)
}