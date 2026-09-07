package dev.pandasystems.pandalib.neoforge.event

import dev.pandasystems.pandalib.core.handles.player.handle
import dev.pandasystems.pandalib.event.events.ServerPlayerConnectionEventContext
import dev.pandasystems.pandalib.event.events.ServerPlayerRespawnEventContextForge
import dev.pandasystems.pandalib.event.events.playerServerAfterRespawn
import dev.pandasystems.pandalib.event.events.playerServerJoin
import dev.pandasystems.pandalib.event.events.playerServerLeave
import dev.pandasystems.pandalib.event.events.server.ServerLevelEventContext
import dev.pandasystems.pandalib.event.events.server.ServerLifecycleEventContext
import dev.pandasystems.pandalib.event.events.server.ServerTickEventContext
import dev.pandasystems.pandalib.event.events.server.postServerTick
import dev.pandasystems.pandalib.event.events.server.preServerTick
import dev.pandasystems.pandalib.event.events.server.serverLevelLoad
import dev.pandasystems.pandalib.event.events.server.serverLevelUnLoad
import dev.pandasystems.pandalib.event.events.server.serverStarted
import dev.pandasystems.pandalib.event.events.server.serverStarting
import dev.pandasystems.pandalib.event.events.server.serverStopped
import dev.pandasystems.pandalib.event.events.server.serverStopping
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.level.LevelEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.server.ServerStoppedEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent

object EventHandler {
	fun init(eventBus: IEventBus) {
		NeoForge.EVENT_BUS.addListener(this::onLevelLoad)
		NeoForge.EVENT_BUS.addListener(this::onLevelUnload)
		NeoForge.EVENT_BUS.addListener(this::onServerStarting)
		NeoForge.EVENT_BUS.addListener(this::onServerStarted)
		NeoForge.EVENT_BUS.addListener(this::onServerStopping)
		NeoForge.EVENT_BUS.addListener(this::onServerStopped)
		NeoForge.EVENT_BUS.addListener(this::onPreServerTick)
		NeoForge.EVENT_BUS.addListener(this::onPostServerTick)
		NeoForge.EVENT_BUS.addListener(this::onPlayerJoin)
		NeoForge.EVENT_BUS.addListener(this::onPlayerLeave)
		NeoForge.EVENT_BUS.addListener(this::onPlayerRespawn)
	}

	fun onLevelLoad(event: LevelEvent.Load) {
		serverLevelLoad(ServerLevelEventContext(event.level))
	}

	fun onLevelUnload(event: LevelEvent.Unload) {
		serverLevelUnLoad(ServerLevelEventContext(event.level))
	}

	fun onServerStarting(event: ServerStartingEvent) {
		serverStarting(ServerLifecycleEventContext(event.server))
	}

	fun onServerStarted(event: ServerStartedEvent) {
		serverStarted(ServerLifecycleEventContext(event.server))
	}

	fun onServerStopping(event: ServerStoppingEvent) {
		serverStopping(ServerLifecycleEventContext(event.server))
	}

	fun onServerStopped(event: ServerStoppedEvent) {
		serverStopped(ServerLifecycleEventContext(event.server))
	}

	fun onPreServerTick(event: ServerTickEvent.Pre) {
		preServerTick(ServerTickEventContext(event.server))
	}

	fun onPostServerTick(event: ServerTickEvent.Post) {
		postServerTick(ServerTickEventContext(event.server))
	}

	fun onPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
		playerServerJoin(ServerPlayerConnectionEventContext(event.entity.handle()))
	}

	fun onPlayerLeave(event: PlayerEvent.PlayerLoggedOutEvent) {
		playerServerLeave(ServerPlayerConnectionEventContext(event.entity.handle()))
	}

	fun onPlayerRespawn(event: PlayerEvent.PlayerRespawnEvent) {
		playerServerAfterRespawn(ServerPlayerRespawnEventContextForge(event.entity.handle()))
	}
}