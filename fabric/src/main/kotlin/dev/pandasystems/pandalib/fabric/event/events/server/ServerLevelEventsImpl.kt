package dev.pandasystems.pandalib.fabric.event.events.server

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.events.server.ServerLevelEventContext
import dev.pandasystems.pandalib.event.events.server.ServerLevelEvents
import dev.pandasystems.pandalib.fabric.event.bindEvent
import net.minecraft.server.level.ServerLevel
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents as FabricServerLevelEvents

@AutoService
class ServerLevelEventsImpl : ServerLevelEvents {
	override val levelLoad: Event<ServerLevelEventContext> = FabricServerLevelEvents.LOAD.bindEvent(
		createListener = { subInvoker ->
			FabricServerLevelEvents.Load { server, level ->
				subInvoker(ServerLevelEventContext(level))
			}
		},
		onInvoke = { ctx, eventInvoker ->
			val serverLevel = ctx.level as ServerLevel
			eventInvoker.onLevelLoad(serverLevel.server, serverLevel)
		}
	)

	override val levelUnLoad: Event<ServerLevelEventContext> = FabricServerLevelEvents.UNLOAD.bindEvent(
		createListener = { subInvoker ->
			FabricServerLevelEvents.Unload { server, level ->
				subInvoker(ServerLevelEventContext(level))
			}
		},
		onInvoke = { ctx, eventInvoker ->
			val serverLevel = ctx.level as ServerLevel
			eventInvoker.onLevelUnload(serverLevel.server, serverLevel)
		}
	)
}