package dev.pandasystems.pandalib.fabric.event.events.server

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.core.handles.player.handle
import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.events.server.ServerPlayerBlockBreakEventContext
import dev.pandasystems.pandalib.event.events.server.ServerPlayerConnectionEventContext
import dev.pandasystems.pandalib.event.events.server.ServerPlayerEvents
import dev.pandasystems.pandalib.event.events.server.ServerPlayerRespawnEventContext
import dev.pandasystems.pandalib.event.events.server.ServerPlayerRespawnEventContextFabric
import dev.pandasystems.pandalib.fabric.event.bindEvent
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents as FabricServerPlayerEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents as FabricPlayerBlockBreakEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents as FabricConnectionEvents
import net.minecraft.world.level.Level

@AutoService
class ServerPlayerEventsImpl : ServerPlayerEvents {
	override val playerServerJoin: Event<ServerPlayerConnectionEventContext> = FabricConnectionEvents.JOIN.bindEvent(
		createListener = { subInvoker ->
			FabricConnectionEvents.Join { handler, _, _ ->
				subInvoker(ServerPlayerConnectionEventContext(handler.player.handle()))
			}
		},
		onInvoke = { ctx, _ ->
			// No direct 1-to-1 synthetic invoker required for connection join
		}
	)

	override val playerServerLeave: Event<ServerPlayerConnectionEventContext> = FabricConnectionEvents.DISCONNECT.bindEvent(
		createListener = { subInvoker ->
			FabricConnectionEvents.Disconnect { handler, _ ->
				subInvoker(ServerPlayerConnectionEventContext(handler.player.handle()))
			}
		},
		onInvoke = { ctx, _ ->
			// No direct 1-to-1 synthetic invoker required for connection disconnect
		}
	)

	override val playerServerAfterRespawn: Event<ServerPlayerRespawnEventContext> = FabricServerPlayerEvents.AFTER_RESPAWN.bindEvent(
		createListener = { subInvoker ->
			FabricServerPlayerEvents.AfterRespawn { oldPlayer, newPlayer, alive ->
				subInvoker(
					ServerPlayerRespawnEventContextFabric(
						player = newPlayer.handle(),
						oldPlayer = oldPlayer.handle(),
						alive = alive
					)
				)
			}
		},
		onInvoke = { ctx, eventInvoker ->
			val fabricCtx = ctx.fabric
			val newPlayer = ctx.player.resolve() as? net.minecraft.server.level.ServerPlayer
			val oldPlayer = fabricCtx?.oldPlayer?.resolve() as? net.minecraft.server.level.ServerPlayer
			if (newPlayer != null && oldPlayer != null && fabricCtx != null) {
				eventInvoker.afterRespawn(oldPlayer, newPlayer, fabricCtx.alive)
			}
		}
	)

	override val playerBlockBreakBefore: Event<ServerPlayerBlockBreakEventContext> = FabricPlayerBlockBreakEvents.BEFORE.bindEvent(
		createListener = { subInvoker ->
			FabricPlayerBlockBreakEvents.Before { level, player, pos, state, entity ->
				val ctx = subInvoker(ServerPlayerBlockBreakEventContext(level, player.handle(), pos, state, entity))
				!ctx.isCanceled
			}
		},
		onInvoke = { ctx, eventInvoker ->
			val player = ctx.player.resolve()
			if (player != null && ctx.level is Level) {
				val allowed = eventInvoker.beforeBlockBreak(ctx.level as Level, player, ctx.pos, ctx.blockState, ctx.blockEntity)
				ctx.isCanceled = !allowed
			}
		}
	)

	override val playerBlockBreakAfter: Event<ServerPlayerBlockBreakEventContext> = FabricPlayerBlockBreakEvents.AFTER.bindEvent(
		createListener = { subInvoker ->
			FabricPlayerBlockBreakEvents.After { level, player, pos, state, entity ->
				subInvoker(ServerPlayerBlockBreakEventContext(level, player.handle(), pos, state, entity))
			}
		},
		onInvoke = { ctx, eventInvoker ->
			val player = ctx.player.resolve()
			if (player != null && ctx.level is Level) {
				eventInvoker.afterBlockBreak(ctx.level as Level, player, ctx.pos, ctx.blockState, ctx.blockEntity)
			}
		}
	)

	override val playerBlockBreakCanceled: Event<ServerPlayerBlockBreakEventContext> = FabricPlayerBlockBreakEvents.CANCELED.bindEvent(
		createListener = { subInvoker ->
			FabricPlayerBlockBreakEvents.Canceled { level, player, pos, state, entity ->
				subInvoker(ServerPlayerBlockBreakEventContext(level, player.handle(), pos, state, entity))
			}
		},
		onInvoke = { ctx, eventInvoker ->
			val player = ctx.player.resolve()
			if (player != null && ctx.level is Level) {
				eventInvoker.onBlockBreakCanceled(ctx.level as Level, player, ctx.pos, ctx.blockState, ctx.blockEntity)
			}
		}
	)
}