/*
 * Copyright (C) 2024 Oliver Froberg (The Panda Oliver)
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.pandamods.pandalib.fabric.networking;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import me.pandamods.pandalib.networking.NetworkContext;
import me.pandamods.pandalib.networking.NetworkReceiver;
import me.pandamods.pandalib.networking.NetworkingRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class NetworkingRegistryImpl implements NetworkingRegistry {
	@Override
	@Environment(EnvType.CLIENT)
	public void registerClientReceiver(ResourceLocation resourceLocation,
									   NetworkReceiver receiver) {
		ClientPlayNetworking.registerGlobalReceiver(resourceLocation, new ClientPlayChannelHandler(receiver));
	}

	@Environment(EnvType.CLIENT)
	private static class ClientPlayChannelHandler implements ClientPlayNetworking.PlayChannelHandler {
		private final NetworkReceiver receiver;

		ClientPlayChannelHandler(NetworkReceiver receiver) {
			this.receiver = receiver;
		}

		@Override
		public void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
			NetworkContext networkContext = new NetworkContext(minecraft.player);
			receiver.receive(networkContext, friendlyByteBuf);
		}
	}

	@Override
	public void registerServerReceiver(ResourceLocation resourceLocation,
									   NetworkReceiver receiver) {
		ServerPlayNetworking.registerGlobalReceiver(resourceLocation, (minecraftServer, serverPlayer,
																	   serverGamePacketListener, friendlyByteBuf,
																	   packetSender) ->
				receiver.receive(new NetworkContext(serverPlayer), friendlyByteBuf));
	}

	@Override
	public void registerBiDirectionalReceiver(ResourceLocation resourceLocation,
											  NetworkReceiver clientReceiver,
											  NetworkReceiver serverReceiver) {
		registerServerReceiver(resourceLocation, serverReceiver);
		if (Platform.getEnvironment() == Env.CLIENT)
			registerClientReceiver(resourceLocation, clientReceiver);
	}
}
