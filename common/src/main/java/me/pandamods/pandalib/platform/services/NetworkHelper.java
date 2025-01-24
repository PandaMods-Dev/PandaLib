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

package me.pandamods.pandalib.platform.services;

import me.pandamods.pandalib.networking.NetworkRegistry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface NetworkHelper extends NetworkRegistry {
	<T extends CustomPacketPayload> void sendToServer(T payload);

	<T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload);

	<T extends CustomPacketPayload> void sendToAllPlayers(T payload);
}
