/*
 * Copyright (C) 2025 Oliver Froberg (The Panda Oliver)
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.pandamods.pandalib.fabric.platform.utils;

import dev.architectury.utils.Env;
import me.pandamods.pandalib.networking.NetworkContext;
import me.pandamods.pandalib.networking.NetworkReceiver;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class ClientPlayChannelHandler {
	public static void receivePlay(NetworkReceiver receiver, Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
		NetworkContext networkContext = new NetworkContext(minecraft.player, Env.CLIENT);
		receiver.receive(networkContext, friendlyByteBuf);
	}
}