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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PacketDistributorImpl implements IPacketDistributor {
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
