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
import me.pandamods.pandalib.fabric.platform.utils.ClientPlayPayloadHandler;
import me.pandamods.pandalib.networking.NetworkContext;
import me.pandamods.pandalib.networking.NetworkReceiver;
import me.pandamods.pandalib.platform.services.NetworkHelper;
import me.pandamods.pandalib.utils.EnvRunner;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class NetworkHelperImpl implements NetworkHelper {
	@Override
	public <T extends CustomPacketPayload> void registerClientReceiver(CustomPacketPayload.Type<T> type,
																	   StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																	   NetworkReceiver<T> receiver) {
		PayloadTypeRegistry.playS2C().register(type, codec);
		EnvRunner.runIf(Env.CLIENT, () -> () ->
				ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> ClientPlayPayloadHandler.receivePlay(receiver, payload, context))
		);
	}

	@Override
	public <T extends CustomPacketPayload> void registerServerReceiver(CustomPacketPayload.Type<T> type,
																	   StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																	   NetworkReceiver<T> receiver) {
		PayloadTypeRegistry.playC2S().register(type, codec);
		ServerPlayNetworking.registerGlobalReceiver(type, (t, context) -> receiver.receive(new NetworkContext(context.player(), Env.SERVER), t));
	}

	@Override
	public <T extends CustomPacketPayload> void registerBiDirectionalReceiver(CustomPacketPayload.Type<T> type,
																			  StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																			  NetworkReceiver<T> clientReceiver, NetworkReceiver<T> serverReceiver) {
		registerServerReceiver(type, codec, serverReceiver);
		registerClientReceiver(type, codec, clientReceiver);
	}

	@Override
	public <T extends CustomPacketPayload> void sendToServer(T payload) {
		ClientPlayNetworking.send(payload);
	}

	@Override
	public <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload) {
		ServerPlayNetworking.send(player, payload);
	}

	@Override
	public <T extends CustomPacketPayload> void sendToAllPlayers(T payload) {
		for (ServerPlayer player : PandaLibFabric.server.getPlayerList().getPlayers()) {
			sendToPlayer(player, payload);
		}
	}
}
