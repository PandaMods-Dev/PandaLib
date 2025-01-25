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

package me.pandamods.pandalib.config;

import me.pandamods.pandalib.config.holders.ClientConfigHolder;
import me.pandamods.pandalib.config.holders.CommonConfigHolder;
import me.pandamods.pandalib.config.holders.ConfigHolder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PandaLibConfig {
	public static Map<Class<? extends ConfigData>, ConfigHolder<? extends ConfigData>> holders = new HashMap<>();

	public static <T extends ConfigData, E extends ConfigHolder<T>> E register(Class<T> configClass, ConfigHolderProvider<T, E> provider) {
		if (holders.containsKey(configClass))
			throw new RuntimeException(String.format("Config %s is already registered", configClass));

		Config config = configClass.getAnnotation(Config.class);
		if (config == null)
			throw new RuntimeException(String.format("%s is not annotated with @Config", configClass));

		E holder = provider.provide(configClass, config);
		holders.put(configClass, holder);
		return holder;
	}
	
	public static <T extends ConfigData> ClientConfigHolder<T> registerClient(Class<T> configClass) {
		// Explicit type is required at compile time for some reason.
		return PandaLibConfig.<T, ClientConfigHolder<T>>register(configClass, ClientConfigHolder::new);
	}

	public static <T extends ConfigData> CommonConfigHolder<T> registerCommon(Class<T> configClass) {
		// Explicit type is required at compile time for some reason.
		return PandaLibConfig.<T, CommonConfigHolder<T>>register(configClass, CommonConfigHolder::new);
	}

	public static Optional<ConfigHolder<? extends ConfigData>> getConfig(ResourceLocation resourceLocation) {
		return holders.values().stream()
				.filter(configHolder -> configHolder.resourceLocation().equals(resourceLocation)).findFirst();
	}

	public static Map<Class<? extends ConfigData>, ConfigHolder<? extends ConfigData>> getConfigs() {
		return holders;
	}

	@SuppressWarnings("unchecked")
	public static <T extends ConfigData> ConfigHolder<T> getConfig(Class<T> config) {
		return (ConfigHolder<T>) holders.get(config);
	}

	public static Map<Class<?>, ConfigHolder<?>> getConfigs(String modId) {
		return holders.entrySet().stream()
				.filter(entry -> entry.getValue().getDefinition().modId().equals(modId))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	/**
	 * Retrieves the configuration screen for the specified mod ID.
	 *
	 * @param modID the mod ID of the configuration
	 * @return the configuration screen
	 * @deprecated The config menu api will be added in the future, this is just here for quick support with future versions,
	 * this method is deprecated and returns null.
	 */
	@Deprecated
	public static Screen getConfigScreen(String modID) {
		return null;
	}

	/**
	 * Retrieves the configuration screen for the specified parent screen and mod ID.
	 *
	 * @param parent the parent screen
	 * @param modID  the mod ID of the configuration
	 * @return the configuration screen
	 * @deprecated The config menu api will be added in the future, this is just here for quick support with future versions,
	 * this method is deprecated and returns null.
	 */
	@Deprecated
	public static Screen getConfigScreen(Screen parent, String modID) {
		return null;
	}

	/**
	 * Retrieves the configuration screen for the specified configClass.
	 *
	 * @param configClass the class of the config data
	 * @return the configuration screen
	 * @deprecated The config menu api will be added in the future, this is just here for quick support with future versions,
	 * this method is deprecated and returns null.
	 */
	@Deprecated
	public static <T extends ConfigData> Screen getConfigScreen(Class<T> configClass) {
		return null;
	}

	/**
	 * Retrieves the configuration screen for the specified configClass.
	 *
	 * @param parent      the parent screen
	 * @param configClass the class of the config data
	 * @return the configuration screen
	 * @deprecated The config menu api will be added in the future, this is just here for quick support with future versions,
	 * this method is deprecated and returns null.
	 */
	@Deprecated
	public static <T extends ConfigData> Screen getConfigScreen(Screen parent, Class<T> configClass) {
		return null;
	}

	public interface ConfigHolderProvider<T extends ConfigData, E extends ConfigHolder<T>> {
		E provide(Class<T> configClass, Config config);
	}
}
