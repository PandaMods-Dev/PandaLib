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

import me.pandamods.pandalib.platform.services.ModLoaderHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModLoaderHelperImpl implements ModLoaderHelper {
	private final Map<String, Mod> mods = new HashMap<>();

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public Mod getMod(String modId) {
		return mods.computeIfAbsent(modId, ModImpl::new);
	}

	@Override
	public List<Mod> getMods() {
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			getMod(mod.getMetadata().getId());
		}
		
		return List.copyOf(mods.values());
	}

	@Override
	public List<String> getModIds() {
		return FabricLoader.getInstance().getAllMods().stream().map(ModContainer::getMetadata).map(ModMetadata::getId).toList();
	}

	private static class ModImpl implements Mod {
		private final ModContainer container;
		private final ModMetadata metadata;
		
		public ModImpl(String modId) {
			this.container = FabricLoader.getInstance().getModContainer(modId).orElseThrow();
			this.metadata = this.container.getMetadata();
		}

		@Override
		public String getId() {
			return metadata.getId();
		}

		@Override
		public String getDisplayName() {
			return metadata.getName();
		}

		@Override
		public String getDescription() {
			return metadata.getDescription();
		}

		@Override
		public List<String> getAuthors() {
			return metadata.getAuthors().stream().map(Person::getName).toList();
		}

		@Override
		public String getVersion() {
			return metadata.getVersion().getFriendlyString();
		}
	}
}
