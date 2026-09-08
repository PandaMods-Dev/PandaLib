package dev.pandasystems.pandalib.neoforge.mixin.events;

import com.llamalad7.mixinextras.sugar.Local;
import dev.pandasystems.pandalib.core.handles.player.PlayerHandleKt;
import dev.pandasystems.pandalib.event.events.server.ServerPlayerBlockBreakEventContext;
import dev.pandasystems.pandalib.event.events.server.ServerPlayerEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ServerPlayerGameMode.class)
public class BlockBreakEventMixin {
	@Final
	@Shadow
	protected ServerPlayer player;

	@Shadow
	protected ServerLevel level;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;"), method = "destroyBlock", cancellable = true)
	private void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local BlockEntity blockEntity, @Local(ordinal = 0) BlockState state) {
		ServerPlayerBlockBreakEventContext result = ServerPlayerEvents.Companion.getPlayerBlockBreakBefore().invoke(new ServerPlayerBlockBreakEventContext(this.level, PlayerHandleKt.handle(this.player), pos, state, blockEntity, false));
		if (result.isCanceled()) cir.setReturnValue(false);
	}

	@Inject(at = @At("RETURN"), method = "destroyBlock")
	private void onBlockBroken(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local BlockEntity blockEntity, @Local(ordinal = 0) BlockState state) {
		if (!cir.getReturnValue()) return;

		ServerPlayerEvents.Companion.getPlayerBlockBreakAfter().invoke(new ServerPlayerBlockBreakEventContext(this.level, PlayerHandleKt.handle(this.player), pos, state, blockEntity, false));
	}

	@Inject(at = @At("RETURN"), method = "destroyBlock")
	private void onBlockBreakCancel(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local BlockEntity blockEntity, @Local(ordinal = 0) BlockState state) {
		if (cir.getReturnValue()) return;

		ServerPlayerEvents.Companion.getPlayerBlockBreakCanceled().invoke(new ServerPlayerBlockBreakEventContext(this.level, PlayerHandleKt.handle(this.player), pos, state, blockEntity, false));
	}
}