package me.pandamods.pandalib.forge.networking;

import me.pandamods.pandalib.networking.IPacketDistributor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class PacketDistributorImpl implements IPacketDistributor {
	@Override
	public void sendToServer(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
		NetworkingRegistryImpl.CHANNELS.get(resourceLocation).sendToServer(byteBuf);
	}

	@Override
	public void sendToPlayer(ServerPlayer player, ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
		NetworkingRegistryImpl.CHANNELS.get(resourceLocation).send(PacketDistributor.PLAYER.with(() -> player), byteBuf);
	}

	@Override
	public void sendToAllPlayers(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
		NetworkingRegistryImpl.CHANNELS.get(resourceLocation).send(PacketDistributor.ALL.noArg(), byteBuf);
	}
}
