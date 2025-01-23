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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.List;

public class ReloadListenerRegistry {
	public static void register(PackType packType, PreparableReloadListener listener) {
		register(packType, listener, null);
	}
	
	public static void register(PackType packType, PreparableReloadListener listener, ResourceLocation id) {
		register(packType, listener, id, List.of());
	}
	
	public static void register(PackType packType, PreparableReloadListener listener, ResourceLocation id, List<ResourceLocation> dependencies) {
		Services.REGISTRATION.registerReloadListener(packType, listener, id, dependencies);
	}

	public static void register(PackType packType, IdentifiableResourceReloadListener listener) {
		Services.REGISTRATION.registerReloadListener(packType, listener, listener.getResourceID(), listener.getResourceDependencies());
	}
}
