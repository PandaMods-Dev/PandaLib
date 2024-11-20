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
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class NetworkingRegistryImpl implements NetworkingRegistry {
	@Override
	@Environment(EnvType.CLIENT)
	public <T extends CustomPacketPayload> void registerClientReceiver(CustomPacketPayload.Type<T> type,
																	   StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																	   NetworkReceiver<T> receiver) {
		PayloadTypeRegistry.playS2C().register(type, codec);
		ClientPlayNetworking.registerGlobalReceiver(type, new ClientPlayPayloadHandler<>(receiver));
	}

	@Environment(EnvType.CLIENT)
	private static class ClientPlayPayloadHandler<T extends CustomPacketPayload> implements ClientPlayNetworking.PlayPayloadHandler<T> {
		private final NetworkReceiver<T> receiver;

		ClientPlayPayloadHandler(NetworkReceiver<T> receiver) {
			this.receiver = receiver;
		}

		@Override
		public void receive(T payload, ClientPlayNetworking.Context context) {
			NetworkContext networkContext = new NetworkContext(context.player(), Env.CLIENT);
			receiver.receive(networkContext, payload);
		}
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
		if (Platform.getEnvironment() == Env.CLIENT)
			registerClientReceiver(type, codec, clientReceiver);
	}
}
