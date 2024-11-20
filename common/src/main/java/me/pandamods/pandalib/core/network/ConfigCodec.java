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
package me.pandamods.pandalib.core.network;

import me.pandamods.pandalib.core.network.packets.ConfigPacketData;
import me.pandamods.pandalib.utils.NBTUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ConfigCodec implements StreamCodec<RegistryFriendlyByteBuf, ConfigPacketData> {
	public static final ConfigCodec INSTANCE = new ConfigCodec();

	@Override
	public ConfigPacketData decode(RegistryFriendlyByteBuf byteBuf) {
		return new ConfigPacketData(byteBuf.readResourceLocation(), NBTUtils.convertTagToJson(byteBuf.readNbt()));
	}

	@Override
	public void encode(RegistryFriendlyByteBuf byteBuf, ConfigPacketData packetData) {
		byteBuf.writeResourceLocation(packetData.resourceLocation());
		byteBuf.writeNbt(NBTUtils.convertJsonToTag(packetData.data()));
	}
}