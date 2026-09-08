package dev.pandasystems.pandalib.event.events.server

import dev.pandasystems.pandalib.core.utils.loadService
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.event
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.LevelAccessor

data class ServerLevelEventContext(val level: LevelAccessor)

interface ServerLevelEvents {
	val levelLoad: Event<ServerLevelEventContext>
	val levelUnLoad: Event<ServerLevelEventContext>

	companion object : ServerLevelEvents by loadService()
}