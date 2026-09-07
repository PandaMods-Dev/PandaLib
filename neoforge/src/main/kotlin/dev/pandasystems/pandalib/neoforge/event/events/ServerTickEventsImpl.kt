package dev.pandasystems.pandalib.neoforge.event.events

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.events.server.ServerTickEventContext
import dev.pandasystems.pandalib.event.events.server.ServerTickEvents
import dev.pandasystems.pandalib.neoforge.event.BoundEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.tick.ServerTickEvent

@AutoService
class ServerTickEventsImpl : ServerTickEvents {
	override val preServerTick: Event<ServerTickEventContext, Unit> = BoundEvent(
		NeoForge.EVENT_BUS,
		ServerTickEvent.Pre::class.java,
		{ ServerTickEventContext(it.server) },
		{ ServerTickEvent.Pre({true}, it.server) },
		{ }
	)

	override val postServerTick: Event<ServerTickEventContext, Unit> = BoundEvent(
		NeoForge.EVENT_BUS,
		ServerTickEvent.Post::class.java,
		{ ServerTickEventContext(it.server) },
		{ ServerTickEvent.Post({true}, it.server) },
		{ }
	)
}