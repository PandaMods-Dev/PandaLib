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

package me.pandamods.pandalib.forge.platform;

import dev.architectury.utils.Env;
import me.pandamods.pandalib.networking.NetworkContext;
import me.pandamods.pandalib.networking.NetworkReceiver;
import me.pandamods.pandalib.platform.services.NetworkHelper;
import me.pandamods.pandalib.utils.EnvRunner;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;

public class NetworkHelperImpl implements NetworkHelper {
	public static final Map<ResourceLocation, SimpleChannel> CHANNELS = new HashMap<>();
	private int id = 0;

	@Override
	public void registerClientReceiver(ResourceLocation resourceLocation,
									   NetworkReceiver receiver) {
		EnvRunner.runIf(Env.CLIENT, () -> () -> registerChannel(resourceLocation, receiver));
	}

	@Override
	public void registerServerReceiver(ResourceLocation resourceLocation,
									   NetworkReceiver receiver) {
		registerChannel(resourceLocation, receiver);
	}

	@Override
	public void registerBiDirectionalReceiver(ResourceLocation resourceLocation,
											  NetworkReceiver clientReceiver,
											  NetworkReceiver serverReceiver) {
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


	@Override
	public void sendToServer(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
		CHANNELS.get(resourceLocation).sendToServer(byteBuf);
	}

	@Override
	public void sendToPlayer(ServerPlayer player, ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
		CHANNELS.get(resourceLocation).send(PacketDistributor.PLAYER.with(() -> player), byteBuf);
	}

	@Override
	public void sendToAllPlayers(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
		CHANNELS.get(resourceLocation).send(PacketDistributor.ALL.noArg(), byteBuf);
	}
}
