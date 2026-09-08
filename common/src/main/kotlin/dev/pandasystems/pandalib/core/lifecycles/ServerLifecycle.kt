package dev.pandasystems.pandalib.core.lifecycles

import dev.pandasystems.pandalib.event.events.server.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer

object ServerLifecycle {
	var serverInstance: MinecraftServer? = null
		internal set

	internal fun initialize() = Unit

	init {
		ServerLifecycleEvents.started.subscribe { context ->
			serverInstance = context.server
		}
		ServerLifecycleEvents.stopped.subscribe { context ->
			if (serverInstance === context.server)
				serverInstance = null
		}
	}
}