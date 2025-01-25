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

package me.pandamods.pandalib.fabric;

import me.pandamods.pandalib.PandaLib;
import me.pandamods.pandalib.event.events.NetworkingEvents;
import me.pandamods.pandalib.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class PandaLibFabric implements ModInitializer {
	public static MinecraftServer server;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> PandaLibFabric.server = server);

		new PandaLib();
		NetworkingEvents.PACKET_PAYLOAD_REGISTRY.invoker().register(Services.NETWORK);
	}
}
