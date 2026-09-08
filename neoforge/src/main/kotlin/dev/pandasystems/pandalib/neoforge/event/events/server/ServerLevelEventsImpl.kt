package dev.pandasystems.pandalib.neoforge.event.events.server

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.events.server.ServerLevelEventContext
import dev.pandasystems.pandalib.event.events.server.ServerLevelEvents
import dev.pandasystems.pandalib.neoforge.event.bindEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.level.LevelEvent

@AutoService(ServerLevelEvents::class)
class ServerLevelEventsImpl : ServerLevelEvents {
	override val levelLoad: Event<ServerLevelEventContext> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerLevelEventContext(it.level) },
		convertFromCtx = { LevelEvent.Load(it.level) }
	)

	override val levelUnLoad: Event<ServerLevelEventContext> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerLevelEventContext(it.level) },
		convertFromCtx = { LevelEvent.Unload(it.level) }
	)
}