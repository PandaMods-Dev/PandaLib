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

package me.pandamods.pandalib.neoforge;

import me.pandamods.pandalib.PandaLib;
import me.pandamods.pandalib.event.events.NetworkingEvents;
import me.pandamods.pandalib.neoforge.networking.NetworkingRegistryImpl;
import me.pandamods.pandalib.neoforge.networking.PacketDistributorImpl;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(PandaLib.MOD_ID)
public class PandaLibNeoForge {
    public PandaLibNeoForge(IEventBus eventBus) {
		eventBus.addListener(FMLCommonSetupEvent.class, PandaLibNeoForge::commonSetup);
		eventBus.addListener(NetworkingRegistryImpl::registerPackets);
    }

	private static void commonSetup(final FMLCommonSetupEvent event) {
		new PandaLib(new PacketDistributorImpl());
	}
}
