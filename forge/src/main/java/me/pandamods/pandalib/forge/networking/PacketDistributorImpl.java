package me.pandamods.pandalib.forge.networking;

import me.pandamods.pandalib.networking.IPacketDistributor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PacketDistributorImpl implements IPacketDistributor {
	@Override
	public void sendToServer(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {

	}

	@Override
	public void sendToPlayer(ServerPlayer player, ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {

	}

	@Override
	public void sendToAllPlayers(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {

	}
}
