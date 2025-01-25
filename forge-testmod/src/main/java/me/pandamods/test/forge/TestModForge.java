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

package me.pandamods.test.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.architectury.utils.Env;
import me.pandamods.pandalib.PandaLib;
import me.pandamods.test.TestMod;
import me.pandamods.test.forge.client.TestModClientForge;
import me.pandamods.pandalib.forge.platform.RegistrationHelperImpl;
import me.pandamods.pandalib.platform.Services;
import me.pandamods.pandalib.utils.EnvRunner;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TestMod.MOD_ID)
public class TestModForge {
    public TestModForge() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		EventBuses.registerModEventBus(TestMod.MOD_ID, eventBus);

		new TestMod();
		EnvRunner.runIf(Env.CLIENT, () -> () -> new TestModClientForge(eventBus));
    }
}
