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

package me.pandamods.testmod.neoforge.client;

import me.pandamods.test.TestMod;
import me.pandamods.test.client.TestModClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;


@Mod(value = TestMod.MOD_ID, dist = Dist.CLIENT)
public class TestModClientNeoForge {
    public TestModClientNeoForge(IEventBus eventBus) {
		eventBus.addListener(TestModClientNeoForge::clientSetup);
    }

	public static void clientSetup(FMLClientSetupEvent event) {
		new TestModClient();
	}
}
