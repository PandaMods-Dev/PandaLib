package me.pandamods.pandalib.core.client.event;

import dev.architectury.event.events.client.ClientPlayerEvent;
import me.pandamods.pandalib.core.network.ConfigNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;

@Environment(EnvType.CLIENT)
public class EventHandlerClient {
	public static void init() {
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(EventHandlerClient::onClientPlayerJoin);
	}

	private static void onClientPlayerJoin(LocalPlayer localPlayer) {
		ConfigNetworking.SyncClientConfigs();
	}
}
