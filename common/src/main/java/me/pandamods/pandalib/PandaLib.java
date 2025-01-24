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

package me.pandamods.pandalib;

import me.pandamods.pandalib.config.Config;
import me.pandamods.pandalib.config.ConfigData;
import me.pandamods.pandalib.config.PandaLibConfig;
import me.pandamods.pandalib.config.holders.CommonConfigHolder;
import me.pandamods.pandalib.core.event.EventHandler;
import me.pandamods.pandalib.core.network.ConfigNetworking;
import me.pandamods.pandalib.event.events.NetworkingEvents;
import me.pandamods.pandalib.platform.Services;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class PandaLib {
    public static final String MOD_ID = "pandalib";
	private static PandaLib instance;

    public PandaLib() {
		NetworkingEvents.PACKET_PAYLOAD_REGISTRY.register(ConfigNetworking::registerPackets);

		EventHandler.init();
		instance = this;
    }

	public static ResourceLocation resourceLocation(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static PandaLib getInstance() {
		return instance;
	}
}