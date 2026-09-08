package dev.pandasystems.pandalib.fabric.event.events.server

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.events.server.ServerTickEventContext
import dev.pandasystems.pandalib.event.events.server.ServerTickEvents
import dev.pandasystems.pandalib.fabric.event.bindEvent
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents as FabricServerTickEvents

@AutoService
class ServerTickEventsImpl : ServerTickEvents {
	override val preServerTick: Event<ServerTickEventContext> = FabricServerTickEvents.START_SERVER_TICK.bindEvent(
		createListener = { subInvoker ->
			FabricServerTickEvents.StartTick { server ->
				subInvoker(ServerTickEventContext(server))
			}
		},
		onInvoke = { ctx, eventInvoker ->
			eventInvoker.onStartTick(ctx.server)
		}
	)

	override val postServerTick: Event<ServerTickEventContext> = FabricServerTickEvents.END_SERVER_TICK.bindEvent(
		createListener = { subInvoker ->
			FabricServerTickEvents.EndTick { server ->
				subInvoker(ServerTickEventContext(server))
			}
		},
		onInvoke = { ctx, eventInvoker ->
			eventInvoker.onEndTick(ctx.server)
		}
	)
}