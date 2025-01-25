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

package me.pandamods.pandalib.fabric.platform;

import com.mojang.serialization.Lifecycle;
import me.pandamods.pandalib.platform.services.RegistrationHelper;
import me.pandamods.pandalib.registry.DeferredObject;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class RegistrationHelperImpl implements RegistrationHelper {
	@Override
	@SuppressWarnings("unchecked")
	public <T> void register(DeferredObject<? extends T> deferredObject, Supplier<? extends T> supplier) {
		Registry.register((Registry<T>) deferredObject.getRegistry(), deferredObject.getId(), supplier.get());
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <T> void registerNewRegistry(Registry<T> registry) {
		ResourceLocation registryName = registry.key().location();
		if (BuiltInRegistries.REGISTRY.containsKey(registryName))
			throw new IllegalStateException("Attempted duplicate registration of registry " + registryName);
		
		((WritableRegistry) BuiltInRegistries.REGISTRY).register(registry.key(), registry, Lifecycle.stable());
	}
	
	@Override
	public void registerReloadListener(PackType packType, PreparableReloadListener listener, ResourceLocation id, List<ResourceLocation> dependencies) {
		ResourceManagerHelper.get(packType).registerReloadListener(new IdentifiableResourceReloadListener() {
			@Override
			public ResourceLocation getFabricId() {
				return id;
			}

			@Override
			public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
												  Executor executor, Executor executor2) {
				return listener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, executor, executor2);
			}

			@Override
			public Collection<ResourceLocation> getFabricDependencies() {
				return dependencies;
			}

			@Override
			public String getName() {
				return listener.getName();
			}
		});
	}
}
