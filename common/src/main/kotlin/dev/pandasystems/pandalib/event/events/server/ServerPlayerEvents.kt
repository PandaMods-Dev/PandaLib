package dev.pandasystems.pandalib.event.events.server

import dev.pandasystems.pandalib.core.handles.player.PlayerHandle
import dev.pandasystems.pandalib.core.utils.loadService
import dev.pandasystems.pandalib.event.Event
import net.minecraft.core.BlockPos
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

data class ServerPlayerConnectionEventContext(val player: PlayerHandle)

sealed interface ServerPlayerRespawnEventContext {
    val player: PlayerHandle

    val fabric: ServerPlayerRespawnEventContextFabric?
        get() = this as? ServerPlayerRespawnEventContextFabric
    val forge: ServerPlayerRespawnEventContextForge?
        get() = this as? ServerPlayerRespawnEventContextForge
}

data class ServerPlayerRespawnEventContextFabric(
    override val player: PlayerHandle,
    val oldPlayer: PlayerHandle,
    val alive: Boolean
) : ServerPlayerRespawnEventContext

data class ServerPlayerRespawnEventContextForge(
    override val player: PlayerHandle
) : ServerPlayerRespawnEventContext

data class ServerPlayerBlockBreakEventContext(
    val level: LevelAccessor,
    val player: PlayerHandle,
    val pos: BlockPos,
    val blockState: BlockState,
    val blockEntity: BlockEntity?,
    var isCanceled: Boolean = false // Replaces old eventCancelable
)

interface ServerPlayerEvents {
    val playerServerJoin: Event<ServerPlayerConnectionEventContext>
    val playerServerLeave: Event<ServerPlayerConnectionEventContext>

    val playerServerAfterRespawn: Event<ServerPlayerRespawnEventContext>

    val playerBlockBreakBefore: Event<ServerPlayerBlockBreakEventContext>
    val playerBlockBreakAfter: Event<ServerPlayerBlockBreakEventContext>
    val playerBlockBreakCanceled: Event<ServerPlayerBlockBreakEventContext>

    companion object : ServerPlayerEvents by loadService()
}