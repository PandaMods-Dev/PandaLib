package me.pandamods.pandalib.forge.networking;

import me.pandamods.pandalib.networking.NetworkReceiver;
import me.pandamods.pandalib.networking.NetworkingRegistry;
import net.minecraft.resources.ResourceLocation;

public class NetworkingRegistryImpl implements NetworkingRegistry {
	@Override
	public void registerClientReceiver(ResourceLocation resourceLocation, NetworkReceiver receiver) {

	}

	@Override
	public void registerServerReceiver(ResourceLocation resourceLocation, NetworkReceiver receiver) {

	}

	@Override
	public void registerBiDirectionalReceiver(ResourceLocation resourceLocation, NetworkReceiver clientReceiver, NetworkReceiver serverReceiver) {

	}
}
