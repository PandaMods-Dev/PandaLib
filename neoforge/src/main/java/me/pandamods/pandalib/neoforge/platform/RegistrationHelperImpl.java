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

package me.pandamods.pandalib.neoforge.platform;

import me.pandamods.pandalib.platform.services.RegistrationHelper;
import me.pandamods.pandalib.registry.DeferredObject;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RegistrationHelperImpl implements RegistrationHelper {
	private final Map<ResourceKey<? extends Registry<?>>, PendingRegistries<?>> pendingRegistries = new HashMap<>();
	private final List<Registry<?>> pendingRegistryTypes = new ArrayList<>();
	private final List<PreparableReloadListener> serverDataReloadListeners = new ArrayList<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T> void register(DeferredObject<? extends T> deferredObject, Supplier<? extends T> supplier) {
		PendingRegistries<T> pending = (PendingRegistries<T>) pendingRegistries
				.computeIfAbsent(deferredObject.getKey().registryKey(), k ->
						new PendingRegistries<>(deferredObject.getKey().registryKey()));
		pending.add(deferredObject, supplier);
	}

	@Override
	public <T> void registerNewRegistry(Registry<T> registry) {
		pendingRegistryTypes.add(registry);
	}

	@Override
	public void registerReloadListener(PackType packType, PreparableReloadListener listener, ResourceLocation id, List<ResourceLocation> dependencies) {
		if (packType == PackType.SERVER_DATA) {
			serverDataReloadListeners.add(listener);
		} else {
			((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(listener);
		}
	}

	public void registerEvent(RegisterEvent event) {
		pendingRegistries.values().forEach(pending -> pending.register(event));
	}
	
	public void registerNewRegistryEvent(NewRegistryEvent event) {
		pendingRegistryTypes.forEach(event::register);
	}
	
	public void addReloadListenerEvent(AddReloadListenerEvent event) {
		serverDataReloadListeners.forEach(event::addListener);
	}

	private static class PendingRegistries<T> {
		private final ResourceKey<? extends Registry<T>> registryKey;

		private final Map<DeferredObject<? extends T>, Supplier<? extends T>> entries = new HashMap<>();

		public PendingRegistries(ResourceKey<? extends Registry<T>> registryKey) {
			this.registryKey = registryKey;
		}

		public void add(DeferredObject<? extends T> deferredObject, Supplier<? extends T> objectSupplier) {
			entries.put(deferredObject, objectSupplier);
		}

		public void register(RegisterEvent event) {
			entries.forEach((deferredObject, supplier) -> {
				event.register(registryKey, deferredObject.getId(), supplier::get);
				deferredObject.bind(false);
			});
		}
	}
}
