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

import me.pandamods.pandalib.PandaLib;
import me.pandamods.pandalib.forge.client.PandaLibClientForge;
import me.pandamods.pandalib.forge.event.EventHandlerImpl;
import me.pandamods.pandalib.forge.platform.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PandaLib.MOD_ID)
public class PandaLibForge {
    public PandaLibForge() {
		EventBuses.registerModEventBus(PandaLib.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		PandaLib.init();

		EventHandlerImpl.register();

		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> PandaLibClientForge::new);
    }
}
