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

package me.pandamods.pandalib.neoforge.networking;

import dev.architectury.platform.Platform;
import me.pandamods.pandalib.event.events.NetworkingEvents;
import me.pandamods.pandalib.networking.NetworkContext;
import me.pandamods.pandalib.networking.NetworkReceiver;
import me.pandamods.pandalib.networking.NetworkingRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkingRegistryImpl extends PayloadRegistrar implements NetworkingRegistry {
	public NetworkingRegistryImpl() {
		super("1");
	}

	@Override
	public <T extends CustomPacketPayload> void registerClientReceiver(CustomPacketPayload.Type<T> type,
																	   StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																	   NetworkReceiver<T> receiver) {
		this.playToClient(type, codec, (arg, ctx) -> receiver.receive(new NetworkContext(ctx.player()), arg));
	}

	@Override
	public <T extends CustomPacketPayload> void registerServerReceiver(CustomPacketPayload.Type<T> type,
																	   StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																	   NetworkReceiver<T> receiver) {
		this.playToServer(type, codec, (arg, ctx) -> receiver.receive(new NetworkContext(ctx.player()), arg));
	}

	@Override
	public <T extends CustomPacketPayload> void registerBiDirectionalReceiver(CustomPacketPayload.Type<T> type,
																			  StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																			  NetworkReceiver<T> clientReceiver,
																			  NetworkReceiver<T> serverReceiver) {
		this.playBidirectional(type, codec, new DirectionalPayloadHandler<>(
				(arg, ctx) -> clientReceiver.receive(new NetworkContext(ctx.player()), arg),
				(arg, ctx) -> serverReceiver.receive(new NetworkContext(ctx.player()), arg)
		));
	}

	public static void registerPackets(final RegisterPayloadHandlersEvent event) {
		NetworkingEvents.PACKET_PAYLOAD_REGISTRY.invoker().register(new NetworkingRegistryImpl());
	}
}
