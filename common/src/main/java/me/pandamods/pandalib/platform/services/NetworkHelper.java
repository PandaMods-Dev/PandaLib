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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface NetworkHelper extends NetworkRegistry {
	void sendToServer(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf);

	void sendToPlayer(ServerPlayer player, ResourceLocation resourceLocation, FriendlyByteBuf byteBuf);

	void sendToAllPlayers(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf);
}
