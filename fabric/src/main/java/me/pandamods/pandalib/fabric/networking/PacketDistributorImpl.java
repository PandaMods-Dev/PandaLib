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

import me.pandamods.pandalib.fabric.PandaLibFabric;
import me.pandamods.pandalib.networking.IPacketDistributor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class PacketDistributorImpl implements IPacketDistributor {
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
