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

package me.pandamods.test;

import com.mojang.serialization.Lifecycle;
import me.pandamods.pandalib.config.PandaLibConfig;
import me.pandamods.pandalib.config.holders.ClientConfigHolder;
import me.pandamods.pandalib.config.holders.CommonConfigHolder;
import me.pandamods.pandalib.registry.DeferredObject;
import me.pandamods.pandalib.registry.DeferredRegister;
import me.pandamods.pandalib.registry.RegistryRegister;
import me.pandamods.test.config.ClientTestConfig;
import me.pandamods.test.config.CommonTestConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class TestMod {
	public static final String MOD_ID = "testmod";
	private static TestMod instance;

	private static final CommonConfigHolder<CommonTestConfig> COMMON_TEST_CONFIG = PandaLibConfig.registerCommon(CommonTestConfig.class);
	private static final ClientConfigHolder<ClientTestConfig> CLIENT_TEST_CONFIG = PandaLibConfig.registerClient(ClientTestConfig.class);

	public static final ResourceKey<Registry<TestRegistry>> TEST_REGISTRY_KEY = ResourceKey.createRegistryKey(TestMod.resourceLocation("test_registry"));
	public static final Registry<TestRegistry> TEST_REGISTRY = RegistryRegister.register(new MappedRegistry<>(TEST_REGISTRY_KEY, Lifecycle.stable(), null));
	
	public static final DeferredRegister<TestRegistry> TEST_REGISTER = DeferredRegister.create(MOD_ID, TEST_REGISTRY);
	public static final DeferredObject<TestRegistry> TEST1 = TEST_REGISTER.register("test1", TestRegistry::new);
	public static final DeferredObject<TestRegistry> TEST2 = TEST_REGISTER.register("test2", TestRegistry::new);

	public TestMod() {
		instance = this;
		TEST_REGISTER.register();
	}

	public static ResourceLocation resourceLocation(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static TestMod getInstance() {
		return instance;
	}

	public static class TestRegistry { }
}
