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

package me.pandamods.pandalib.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface NetworkingRegistry {
	<T extends CustomPacketPayload> void registerClientReceiver(CustomPacketPayload.Type<T> type,
																StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																NetworkReceiver<T> receiver);

	<T extends CustomPacketPayload> void registerServerReceiver(CustomPacketPayload.Type<T> type,
																StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																NetworkReceiver<T> receiver);

	<T extends CustomPacketPayload> void registerBiDirectionalReceiver(CustomPacketPayload.Type<T> type,
																	   StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																	   NetworkReceiver<T> clientReceiver,
																	   NetworkReceiver<T> serverReceiver);
}
