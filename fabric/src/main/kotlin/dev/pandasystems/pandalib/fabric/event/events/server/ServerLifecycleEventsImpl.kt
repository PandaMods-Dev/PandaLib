package dev.pandasystems.pandalib.fabric.event.events.server

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.events.server.ServerLifecycleEventContext
import dev.pandasystems.pandalib.event.events.server.ServerLifecycleEvents
import dev.pandasystems.pandalib.fabric.event.bindEvent
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents as FabricServerLifecycleEvents

@AutoService
class ServerLifecycleEventsImpl : ServerLifecycleEvents {
	override val starting: Event<ServerLifecycleEventContext> = FabricServerLifecycleEvents.SERVER_STARTING.bindEvent(
		createListener = { subInvoker ->
			FabricServerLifecycleEvents.ServerStarting { server ->
				subInvoker(ServerLifecycleEventContext(server))
			}
		},
		onInvoke = { ctx, eventInvoker ->
			eventInvoker.onServerStarting(ctx.server)
		}
	)

	override val started: Event<ServerLifecycleEventContext> = FabricServerLifecycleEvents.SERVER_STARTED.bindEvent(
		createListener = { subInvoker ->
			FabricServerLifecycleEvents.ServerStarted { server ->
				subInvoker(ServerLifecycleEventContext(server))
			}
		},
		onInvoke = { ctx, eventInvoker ->
			eventInvoker.onServerStarted(ctx.server)
		}
	)

	override val stopping: Event<ServerLifecycleEventContext> = FabricServerLifecycleEvents.SERVER_STOPPING.bindEvent(
		createListener = { subInvoker ->
			FabricServerLifecycleEvents.ServerStopping { server ->
				subInvoker(ServerLifecycleEventContext(server))
			}
		},
		onInvoke = { ctx, eventInvoker ->
			eventInvoker.onServerStopping(ctx.server)
		}
	)

	override val stopped: Event<ServerLifecycleEventContext> = FabricServerLifecycleEvents.SERVER_STOPPED.bindEvent(
		createListener = { subInvoker ->
			FabricServerLifecycleEvents.ServerStopped { server ->
				subInvoker(ServerLifecycleEventContext(server))
			}
		},
		onInvoke = { ctx, eventInvoker ->
			eventInvoker.onServerStopped(ctx.server)
		}
	)
}