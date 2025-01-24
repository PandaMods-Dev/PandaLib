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

import me.pandamods.pandalib.platform.Services;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class PacketDistributor {
	public static <T extends CustomPacketPayload> void sendToServer(T payload) {
		Services.NETWORK.sendToServer(payload);
	}

	public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload) {
		Services.NETWORK.sendToPlayer(player, payload);
	}

	public static <T extends CustomPacketPayload> void sendToAllPlayers(T payload) {
		Services.NETWORK.sendToAllPlayers(payload);
	}
}
