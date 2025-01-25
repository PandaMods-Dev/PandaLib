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

package me.pandamods.pandalib.forge.platform;

import com.mojang.serialization.Lifecycle;
import me.pandamods.pandalib.platform.services.RegistrationHelper;
import me.pandamods.pandalib.registry.DeferredObject;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.registries.RegisterEvent;

import java.util.*;
import java.util.function.Supplier;

public class RegistrationHelperImpl implements RegistrationHelper {
	private final Map<ResourceKey<? extends Registry<?>>, PendingRegistries<?>> pendingRegistries = new HashMap<>();
	private final List<Registry<?>> pendingRegistryTypes = new ArrayList<>();
	private final List<PreparableReloadListener> serverDataReloadListeners = new ArrayList<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T> void register(DeferredObject<? extends T> deferredObject, Supplier<? extends T> supplier) {
		PendingRegistries<T> pending = (PendingRegistries<T>) pendingRegistries
				.computeIfAbsent(deferredObject.getRegistryKey(), k ->
						new PendingRegistries<>((ResourceKey<? extends Registry<T>>) deferredObject.getRegistryKey()));
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void registerNewRegistries() {
		for (Registry registry : pendingRegistryTypes) {
			((WritableRegistry) Registry.REGISTRY).register(registry.key(), registry, Lifecycle.stable());
		}
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
