package me.pandamods.pandalib.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import me.pandamods.pandalib.config.api.holders.ClientConfigHolder;
import me.pandamods.pandalib.config.api.holders.CommonConfigHolder;
import me.pandamods.pandalib.config.api.holders.ConfigHolder;
import me.pandamods.pandalib.config.api.ConfigRegistry;
import me.pandamods.pandalib.config.api.ConfigType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ConfigPacketClient {
	public static void sendToServer() {
		ConfigRegistry.getConfigs().values().stream()
				.filter(configHolder -> configHolder instanceof ClientConfigHolder<?> && configHolder.getDefinition().synchronize())
				.forEach(configHolder -> {
					configHolder.logger.info("Sending server client config's");
					FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
					byteBuf.writeResourceLocation(new ResourceLocation(configHolder.getDefinition().modId(), configHolder.getDefinition().name()));
					byteBuf.writeByteArray(new Gson().toJson(configHolder.get()).getBytes());
					NetworkManager.sendToServer(PacketHandler.CONFIG_PACKET, byteBuf);
				});
	}

	public static void configReceiver(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
		ResourceLocation resourceLocation = buf.readResourceLocation();
		ConfigRegistry.getConfig(resourceLocation).ifPresent(configHolder -> {
			if (configHolder instanceof CommonConfigHolder<?> commonConfigHolder) {
				configHolder.logger.info("Received config '{}' from server", configHolder.name());
				byte[] configBytes = buf.readByteArray();
				commonConfigHolder.setCommonConfig(configBytes);
			}
		});
	}
}