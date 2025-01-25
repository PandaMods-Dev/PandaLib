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

package me.pandamods.pandalib.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.architectury.utils.Env;
import me.pandamods.pandalib.PandaLib;
import me.pandamods.pandalib.event.events.NetworkingEvents;
import me.pandamods.pandalib.forge.client.PandaLibClientForge;
import me.pandamods.pandalib.forge.platform.NetworkHelperImpl;
import me.pandamods.pandalib.forge.platform.RegistrationHelperImpl;
import me.pandamods.pandalib.platform.Services;
import me.pandamods.pandalib.utils.EnvRunner;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PandaLib.MOD_ID)
public class PandaLibForge {
    public PandaLibForge() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		EventBuses.registerModEventBus(PandaLib.MOD_ID, eventBus);

		new PandaLib();
		if (Services.REGISTRATION instanceof RegistrationHelperImpl helper) {
			eventBus.addListener(helper::registerEvent);
			MinecraftForge.EVENT_BUS.addListener(helper::addReloadListenerEvent);
		}
		NetworkingEvents.PACKET_PAYLOAD_REGISTRY.invoker().register(new NetworkHelperImpl());

		EnvRunner.runIf(Env.CLIENT, () -> () -> new PandaLibClientForge(eventBus));
    }
}
