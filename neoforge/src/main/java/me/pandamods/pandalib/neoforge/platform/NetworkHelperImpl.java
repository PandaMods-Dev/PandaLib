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

package me.pandamods.pandalib.neoforge.platform;

import dev.architectury.utils.Env;
import me.pandamods.pandalib.event.events.NetworkingEvents;
import me.pandamods.pandalib.networking.NetworkContext;
import me.pandamods.pandalib.networking.NetworkReceiver;
import me.pandamods.pandalib.platform.Services;
import me.pandamods.pandalib.platform.services.NetworkHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHelperImpl extends PayloadRegistrar implements NetworkHelper {
	public NetworkHelperImpl() {
		super("1");
	}

	@Override
	public <T extends CustomPacketPayload> void registerClientReceiver(CustomPacketPayload.Type<T> type,
																	   StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																	   NetworkReceiver<T> receiver) {
		this.playToClient(type, codec, (arg, ctx) -> receiver.receive(new NetworkContext(ctx.player(), Env.CLIENT), arg));
	}

	@Override
	public <T extends CustomPacketPayload> void registerServerReceiver(CustomPacketPayload.Type<T> type,
																	   StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																	   NetworkReceiver<T> receiver) {
		this.playToServer(type, codec, (arg, ctx) -> receiver.receive(new NetworkContext(ctx.player(), Env.SERVER), arg));
	}

	@Override
	public <T extends CustomPacketPayload> void registerBiDirectionalReceiver(CustomPacketPayload.Type<T> type,
																			  StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
																			  NetworkReceiver<T> clientReceiver,
																			  NetworkReceiver<T> serverReceiver) {
		this.playBidirectional(type, codec, new DirectionalPayloadHandler<>(
				(arg, ctx) -> clientReceiver.receive(new NetworkContext(ctx.player(), Env.CLIENT), arg),
				(arg, ctx) -> serverReceiver.receive(new NetworkContext(ctx.player(), Env.SERVER), arg)
		));
	}

	public static void registerPackets(final RegisterPayloadHandlersEvent event) {
		NetworkingEvents.PACKET_PAYLOAD_REGISTRY.invoker().register(Services.NETWORK);
	}

	@Override
	public <T extends CustomPacketPayload> void sendToServer(T payload) {
		PacketDistributor.sendToServer(payload);
	}

	@Override
	public <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload) {
		PacketDistributor.sendToPlayer(player, payload);
	}

	@Override
	public <T extends CustomPacketPayload> void sendToAllPlayers(T payload) {
		PacketDistributor.sendToAllPlayers(payload);
	}
}
