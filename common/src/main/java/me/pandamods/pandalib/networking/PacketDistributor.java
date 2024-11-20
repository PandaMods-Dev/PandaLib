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

import me.pandamods.pandalib.PandaLib;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PacketDistributor {
	public static void sendToServer(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
		PandaLib.getInstance().packetDistributor.sendToServer(resourceLocation, byteBuf);
	}

	public static void sendToPlayer(ServerPlayer player, ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
		PandaLib.getInstance().packetDistributor.sendToPlayer(player, resourceLocation, byteBuf);
	}

	public static void sendToAllPlayers(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
		PandaLib.getInstance().packetDistributor.sendToAllPlayers(resourceLocation, byteBuf);
	}
}
