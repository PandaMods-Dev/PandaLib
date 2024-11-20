package me.pandamods.pandalib.forge.networking;

import dev.architectury.utils.Env;
import me.pandamods.pandalib.networking.NetworkContext;
import me.pandamods.pandalib.networking.NetworkReceiver;
import me.pandamods.pandalib.networking.NetworkingRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;

public class NetworkingRegistryImpl implements NetworkingRegistry {
	public static final Map<ResourceLocation, SimpleChannel> CHANNELS = new HashMap<>();
	private int id = 0;

	@Override
	public void registerClientReceiver(ResourceLocation resourceLocation, NetworkReceiver receiver) {
		registerChannel(resourceLocation, receiver);
	}

	@Override
	public void registerServerReceiver(ResourceLocation resourceLocation, NetworkReceiver receiver) {
		registerChannel(resourceLocation, receiver);
	}

	@Override
	public void registerBiDirectionalReceiver(ResourceLocation resourceLocation, NetworkReceiver clientReceiver, NetworkReceiver serverReceiver) {
		registerChannel(resourceLocation, (ctx, byteBuf) -> {
			switch (ctx.getDirection()) {
				case CLIENT -> clientReceiver.receive(ctx, byteBuf);
				case SERVER -> serverReceiver.receive(ctx, byteBuf);
				default -> throw new IllegalStateException("Unexpected value: " + ctx.getDirection());
			}
		});
	}

	private void registerChannel(ResourceLocation resourceLocation, NetworkReceiver receiver) {
		if (CHANNELS.containsKey(resourceLocation))
			throw new IllegalArgumentException(String.format("Networking channel '%s' has already been registered", resourceLocation));

		SimpleChannel channel = NetworkRegistry.newSimpleChannel(resourceLocation, () -> "1", s -> true, s -> true);
		channel.registerMessage(id++, FriendlyByteBuf.class,
				(o, byteBuf) -> byteBuf.writeBytes(o),
				byteBuf -> byteBuf,
				(o, contextSupplier) -> receiver.receive(new NetworkContext(
						contextSupplier.get().getSender(),
						switch (contextSupplier.get().getDirection()) {
							case PLAY_TO_CLIENT, LOGIN_TO_CLIENT -> Env.CLIENT;
							case PLAY_TO_SERVER, LOGIN_TO_SERVER -> Env.SERVER;
						}
				), o));
		CHANNELS.put(resourceLocation, channel);
	}
}
