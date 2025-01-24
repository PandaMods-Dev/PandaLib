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

package me.pandamods.testmod.neoforge;

import dev.architectury.utils.Env;
import me.pandamods.pandalib.utils.EnvRunner;
import me.pandamods.test.TestMod;
import me.pandamods.testmod.neoforge.client.TestModClientNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(TestMod.MOD_ID)
public class TestModNeoForge {
    public TestModNeoForge(IEventBus eventBus) {
		new TestMod();

		EnvRunner.runIf(Env.CLIENT, () -> () -> new TestModClientNeoForge(eventBus));
    }
}
