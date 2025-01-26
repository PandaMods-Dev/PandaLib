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

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class DeferredObject<T> implements Supplier<T> {
	private final ResourceKey<?> key;

	private Holder<?> holder = null;

	public DeferredObject(ResourceKey<?> key) {
		this.key = key;
		bind(false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get() {
		bind(true);
		if (this.holder == null) {
			throw new NullPointerException("Trying to access unbound value: " + this.key);
		}

		return (T) this.holder.value();
	}

	@SuppressWarnings("unchecked")
	public final <R> void bind(boolean throwOnMissingRegistry) {
		if (this.holder != null) return;

		Registry<R> registry = (Registry<R>) getRegistry();
		if (registry != null) {
			this.holder = registry.getHolder((ResourceKey<R>) this.key).orElse(null);
		} else if (throwOnMissingRegistry) {
			throw new IllegalStateException("Registry not present for " + this + ": " + this.key.registry());
		}
	}

	public ResourceKey<?> getKey() {
		return key;
	}

	public ResourceKey<? extends Registry<?>> getRegistryKey() {
		return ResourceKey.createRegistryKey(key.registry());
	}

	public Registry<?> getRegistry() {
		return BuiltInRegistries.REGISTRY.get(getKey().registry());
	}

	public ResourceLocation getId() {
		return getKey().location();
	}

	public boolean isBound() {
		bind(false);
		return this.holder != null && this.holder.isBound();
	}
}
