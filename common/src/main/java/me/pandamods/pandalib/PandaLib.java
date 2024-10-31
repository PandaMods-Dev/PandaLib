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

import me.pandamods.pandalib.core.event.EventHandler;
import me.pandamods.pandalib.core.network.ConfigNetworking;
import net.minecraft.resources.ResourceLocation;

public class PandaLib {
    public static final String MOD_ID = "pandalib";

//	private static final CommonConfigHolder<TestConfig> TEST_CONFIG = PandaLibConfig.registerCommon(TestConfig.class);
//	private static final ClientConfigHolder<TestConfig> TEST_CONFIG = PandaLibConfig.registerClient(TestConfig.class);

    public static void init() {
		ConfigNetworking.registerPackets();
		EventHandler.init();
    }

	public static ResourceLocation resourceLocation(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

//	@Config(modId = MOD_ID, synchronize = true, name = "test")
//	public static class TestConfig implements ConfigData {
//		public String aString = "Hello World!";
//		public float aFloat = 1.0f;
//		public int anInt = 1;
//		public boolean aBoolean = true;
//	}
}