package dev.pandasystems.pandalib.neoforge.event.events

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.events.server.ServerTickEventContext
import dev.pandasystems.pandalib.event.events.server.ServerTickEvents
import dev.pandasystems.pandalib.neoforge.event.bindEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.tick.ServerTickEvent

@AutoService
class ServerTickEventsImpl : ServerTickEvents {
	override val preServerTick: Event<ServerTickEventContext, Unit> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerTickEventContext(it.server) },
		convertFromCtx = { ServerTickEvent.Pre({true}, it.server) },
		supplyReturn = { }
	)

	override val postServerTick: Event<ServerTickEventContext, Unit> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerTickEventContext(it.server) },
		convertFromCtx = { ServerTickEvent.Post({true}, it.server) },
		supplyReturn = { }
	)
}