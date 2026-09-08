package dev.pandasystems.pandalib.neoforge.event.events.server

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.core.handles.player.handle
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.event
import dev.pandasystems.pandalib.event.events.server.ServerPlayerBlockBreakEventContext
import dev.pandasystems.pandalib.event.events.server.ServerPlayerConnectionEventContext
import dev.pandasystems.pandalib.event.events.server.ServerPlayerEvents
import dev.pandasystems.pandalib.event.events.server.ServerPlayerRespawnEventContext
import dev.pandasystems.pandalib.event.events.server.ServerPlayerRespawnEventContextForge
import dev.pandasystems.pandalib.neoforge.event.bindEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerEvent

@AutoService(ServerPlayerEvents::class)
class ServerPlayerEventsImpl : ServerPlayerEvents {
	override val playerServerJoin: Event<ServerPlayerConnectionEventContext> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerPlayerConnectionEventContext(it.entity.handle()) },
		convertFromCtx = { PlayerEvent.PlayerLoggedInEvent(it.player.resolve()!!) }
	)

	override val playerServerLeave: Event<ServerPlayerConnectionEventContext> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerPlayerConnectionEventContext(it.entity.handle()) },
		convertFromCtx = { PlayerEvent.PlayerLoggedOutEvent(it.player.resolve()!!) }
	)

	override val playerServerAfterRespawn: Event<ServerPlayerRespawnEventContext> = NeoForge.EVENT_BUS.bindEvent(
		convertToCtx = { ServerPlayerRespawnEventContextForge(it.entity.handle()) },
		convertFromCtx = { PlayerEvent.PlayerRespawnEvent(it.player.resolve()!!, false) }
	)

	override val playerBlockBreakBefore: Event<ServerPlayerBlockBreakEventContext> by event()
	override val playerBlockBreakAfter: Event<ServerPlayerBlockBreakEventContext> by event()
	override val playerBlockBreakCanceled: Event<ServerPlayerBlockBreakEventContext> by event()
}