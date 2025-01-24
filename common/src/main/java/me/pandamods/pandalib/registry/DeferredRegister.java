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

package me.pandamods.pandalib.registry;

import me.pandamods.pandalib.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DeferredRegister<T> {
	private final String namespace;
	private final ResourceKey<? extends Registry<T>> registryKey;
	private final Map<DeferredObject<? extends T>, Supplier<? extends T>> entries = new HashMap<>();

	public static <T> DeferredRegister<T> create(String namespace, ResourceLocation registryLocation) {
		return create(namespace, ResourceKey.createRegistryKey(registryLocation));
	}

	public static <T> DeferredRegister<T> create(String namespace, Registry<T> registry) {
		return create(namespace, registry.key());
	}

	public static <T> DeferredRegister<T> create(String namespace, ResourceKey<? extends Registry<T>> registryKey) {
		return new DeferredRegister<>(namespace, registryKey);
	}

	private DeferredRegister(String namespace, ResourceKey<? extends Registry<T>> registryKey) {
		this.namespace = namespace;
		this.registryKey = registryKey;
	}

	public <R extends T> DeferredObject<R> register(String name, Function<ResourceKey<T>, R> registryFunc) {
		return register(ResourceLocation.fromNamespaceAndPath(namespace, name), registryFunc);
	}

	public <R extends T> DeferredObject<R> register(String name, Supplier<R> registrySup) {
		return register(ResourceLocation.fromNamespaceAndPath(namespace, name), registrySup);
	}

	public <R extends T> DeferredObject<R> register(ResourceLocation name, Function<ResourceKey<T>, R> registryFunc) {
		ResourceKey<T> key = ResourceKey.create(registryKey, name);
		return register(key, () -> registryFunc.apply(key));
	}

	public <R extends T> DeferredObject<R> register(ResourceLocation name, Supplier<R> registrySup) {
		return register(ResourceKey.create(registryKey, name), registrySup);
	}

	private <R extends T> DeferredObject<R> register(ResourceKey<T> resourceKey, Supplier<R> registrySup) {
		DeferredObject<R> deferredObject = new DeferredObject<>(resourceKey);
		entries.put(deferredObject, registrySup);
		return deferredObject;
	}

	public void register() {
		entries.forEach(Services.REGISTRATION::register);
	}
}
