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

package me.pandamods.pandalib.core.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import me.pandamods.pandalib.PandaLib;
import me.pandamods.pandalib.config.ConfigData;
import me.pandamods.pandalib.config.PandaLibConfig;
import me.pandamods.pandalib.config.holders.ClientConfigHolder;
import me.pandamods.pandalib.config.holders.CommonConfigHolder;
import me.pandamods.pandalib.utils.NBTUtils;
import me.pandamods.pandalib.utils.NetworkHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ConfigNetworking {
	public static final ResourceLocation CONFIG_PACKET = PandaLib.resourceLocation("config_sync");

	public static void registerPackets() {
		NetworkHelper.registerS2C(CONFIG_PACKET, ConfigNetworking::CommonConfigReceiver);

		NetworkHelper.registerC2S(CONFIG_PACKET, ConfigNetworking::ClientConfigReceiver);
	}

	public static void SyncCommonConfigs(ServerPlayer serverPlayer) {
		PandaLibConfig.getConfigs().values().stream()
				.filter(configHolder -> configHolder instanceof CommonConfigHolder<?> && configHolder.shouldSynchronize())
				.forEach(configHolder -> SyncCommonConfig(serverPlayer, (CommonConfigHolder<?>) configHolder));
	}

	public static void SyncCommonConfig(ServerPlayer serverPlayer, CommonConfigHolder<?> holder) {
		holder.logger.info("Sending common config '{}' to {}", holder.resourceLocation().toString(), serverPlayer.getDisplayName().getString());
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeResourceLocation(holder.resourceLocation());
		byteBuf.writeNbt((CompoundTag) NBTUtils.convertJsonToTag(holder.getGson().toJsonTree(holder.get())));
		NetworkManager.sendToPlayer(serverPlayer, CONFIG_PACKET, byteBuf);
	}

	public static void SyncClientConfigs() {
		PandaLibConfig.getConfigs().values().stream()
				.filter(configHolder -> configHolder instanceof ClientConfigHolder<?> && configHolder.shouldSynchronize())
				.forEach(configHolder -> SyncClientConfig((ClientConfigHolder<?>) configHolder));
	}

	public static void SyncClientConfig(ClientConfigHolder<?> holder) {
		holder.logger.info("Sending client config '{}' to server", holder.resourceLocation().toString());
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeResourceLocation(holder.resourceLocation());
		byteBuf.writeNbt((CompoundTag) NBTUtils.convertJsonToTag(holder.getGson().toJsonTree(holder.get())));
		NetworkManager.sendToServer(CONFIG_PACKET, byteBuf);
	}

	private static void ClientConfigReceiver(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
		ResourceLocation resourceLocation = buf.readResourceLocation();
		PandaLibConfig.getConfig(resourceLocation).ifPresent(configHolder -> {
			if (configHolder instanceof ClientConfigHolder<? extends ConfigData> clientConfigHolder) {
				configHolder.logger.info("Received client config '{}' from {}",
						configHolder.resourceLocation().toString(), context.getPlayer().getDisplayName().getString());
				clientConfigHolder.putConfig(context.getPlayer(),
						configHolder.getGson().fromJson(NBTUtils.convertTagToJson(buf.readNbt()), configHolder.getConfigClass()));
			}
		});
	}

	private static void CommonConfigReceiver(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
		ResourceLocation resourceLocation = buf.readResourceLocation();
		PandaLibConfig.getConfig(resourceLocation).ifPresent(configHolder -> {
			if (configHolder instanceof CommonConfigHolder<? extends ConfigData> commonConfigHolder) {
				configHolder.logger.info("Received common config '{}' from server", configHolder.resourceLocation().toString());
				commonConfigHolder.setCommonConfig(
						configHolder.getGson().fromJson(NBTUtils.convertTagToJson(buf.readNbt()), configHolder.getConfigClass()));
			}
		});
	}
}
