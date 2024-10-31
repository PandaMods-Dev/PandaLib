package me.pandamods.pandalib.utils;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.resources.ResourceLocation;

public class NetworkHelper {
	public static void registerS2C(ResourceLocation id, NetworkManager.NetworkReceiver receiver) {
		if (Platform.getEnvironment().equals(Env.CLIENT))
			NetworkManager.registerReceiver(NetworkManager.serverToClient(), id, receiver);
	}

	public static void registerC2S(ResourceLocation id, NetworkManager.NetworkReceiver receiver) {
		NetworkManager.registerReceiver(NetworkManager.clientToServer(), id, receiver);
	}
}