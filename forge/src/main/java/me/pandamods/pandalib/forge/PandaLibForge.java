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

package me.pandamods.pandalib.forge;

import dev.architectury.platform.forge.EventBuses;
import me.pandamods.pandalib.PandaLib;
import me.pandamods.pandalib.event.events.NetworkingEvents;
import me.pandamods.pandalib.forge.client.PandaLibClientForge;
import me.pandamods.pandalib.forge.networking.NetworkingRegistryImpl;
import me.pandamods.pandalib.forge.networking.PacketDistributorImpl;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PandaLib.MOD_ID)
public class PandaLibForge {
    public PandaLibForge() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		EventBuses.registerModEventBus(PandaLib.MOD_ID, eventBus);
		eventBus.addListener(PandaLibForge::commonSetup);
		eventBus.addListener(PandaLibClientForge::clientSetup);
    }

	public static void commonSetup(final FMLCommonSetupEvent event) {
		new PandaLib(new PacketDistributorImpl());
		NetworkingEvents.PACKET_PAYLOAD_REGISTRY.invoker().register(new NetworkingRegistryImpl());
	}
}
