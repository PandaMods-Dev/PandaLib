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

package me.pandamods.pandalib.fabric.platform;

import dev.architectury.utils.Env;
import me.pandamods.pandalib.fabric.PandaLibFabric;
import me.pandamods.pandalib.fabric.platform.utils.ClientPlayChannelHandler;
import me.pandamods.pandalib.networking.NetworkContext;
import me.pandamods.pandalib.networking.NetworkReceiver;
import me.pandamods.pandalib.platform.services.NetworkHelper;
import me.pandamods.pandalib.utils.EnvRunner;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class NetworkHelperImpl implements NetworkHelper {
	@Override
	public void registerClientReceiver(ResourceLocation resourceLocation,
									   NetworkReceiver receiver) {
		EnvRunner.runIf(Env.CLIENT, () -> () ->
				ClientPlayNetworking.registerGlobalReceiver(resourceLocation, (minecraft, clientPacketListener,
																			   friendlyByteBuf, packetSender) -> {
					ClientPlayChannelHandler.receivePlay(receiver, minecraft, clientPacketListener, friendlyByteBuf, packetSender);
				})
		);
	}

	@Override
	public void registerServerReceiver(ResourceLocation resourceLocation,
									   NetworkReceiver receiver) {
		ServerPlayNetworking.registerGlobalReceiver(resourceLocation, (minecraftServer, serverPlayer,
																	   serverGamePacketListener, friendlyByteBuf,
																	   packetSender) ->
				receiver.receive(new NetworkContext(serverPlayer, Env.SERVER), friendlyByteBuf));
	}

	@Override
	public void registerBiDirectionalReceiver(ResourceLocation resourceLocation,
											  NetworkReceiver clientReceiver,
											  NetworkReceiver serverReceiver) {
		registerServerReceiver(resourceLocation, serverReceiver);
		registerClientReceiver(resourceLocation, clientReceiver);
	}

	@Override
	public void sendToServer(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
		ClientPlayNetworking.send(resourceLocation, byteBuf);
	}

	@Override
	public void sendToPlayer(ServerPlayer player, ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
		ServerPlayNetworking.send(player, resourceLocation, byteBuf);
	}

	@Override
	public void sendToAllPlayers(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
		for (ServerPlayer player : PandaLibFabric.server.getPlayerList().getPlayers()) {
			sendToPlayer(player, resourceLocation, byteBuf);
		}
	}
}
