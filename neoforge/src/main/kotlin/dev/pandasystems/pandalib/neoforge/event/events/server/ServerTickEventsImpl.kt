package dev.pandasystems.pandalib.neoforge.event.events.server

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.events.server.ServerTickEventContext
import dev.pandasystems.pandalib.event.events.server.ServerTickEvents
import dev.pandasystems.pandalib.neoforge.event.bindEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.tick.ServerTickEvent

@AutoService(ServerTickEvents::class)
class ServerTickEventsImpl : ServerTickEvents {
	override val preServerTick: Event<ServerTickEventContext> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerTickEventContext(it.server) },
		convertFromCtx = { ServerTickEvent.Pre({true}, it.server) }
	)

	override val postServerTick: Event<ServerTickEventContext> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerTickEventContext(it.server) },
		convertFromCtx = { ServerTickEvent.Post({true}, it.server) }
	)
}