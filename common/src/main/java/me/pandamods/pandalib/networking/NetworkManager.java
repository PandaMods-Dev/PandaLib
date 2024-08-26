/*
 * Copyright (C) 2024 Oliver Froberg (The Panda Oliver)
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 * You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.pandamods.pandalib.networking;

import me.pandamods.pandalib.platform.Services;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class NetworkManager {
	public static final NetworkHandler INSTANCE = Services.PLATFORM.getNetwork();

	public static <T extends CustomPacketPayload> void sendToServer(T payload) {
		INSTANCE.sendToServer(payload);
	}

	public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload) {
		INSTANCE.sendToPlayer(player, payload);
	}

	public static <T extends CustomPacketPayload> void sendToPlayers(Iterable<ServerPlayer> players, T payload) {
		for (ServerPlayer player : players) {
			sendToPlayer(player, payload);
		}
	}

	public static <T extends CustomPacketPayload> void registerC2SReceiver(CustomPacketPayload.Type<T> type,
																		   StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																		   NetworkReceiver<T> receiver) {
		INSTANCE.registerC2SReceiver(type, codec, receiver);
	}

	public static <T extends CustomPacketPayload> void registerS2CReceiver(CustomPacketPayload.Type<T> type,
																		   StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																		   NetworkReceiver<T> receiver) {
		INSTANCE.registerS2CReceiver(type, codec, receiver);
	}
}