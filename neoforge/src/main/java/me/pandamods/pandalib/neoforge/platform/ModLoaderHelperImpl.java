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

import me.pandamods.pandalib.platform.services.ModLoaderHelper;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.*;

public class ModLoaderHelperImpl implements ModLoaderHelper {
	private final Map<String, Mod> mods = new HashMap<>();
	
	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public Mod getMod(String modId) {
		return mods.computeIfAbsent(modId, ModImpl::new);
	}

	@Override
	public List<Mod> getMods() {
		for (IModInfo mod : ModList.get().getMods()) {
			getMod(mod.getModId());
		}

		return List.copyOf(mods.values());
	}

	@Override
	public List<String> getModIds() {
		return ModList.get().getMods().stream().map(IModInfo::getModId).toList();
	}

	private static class ModImpl implements Mod {
		private final ModContainer container;
		private final IModInfo info;
		
		public ModImpl(String modId) {
			this.container = ModList.get().getModContainerById(modId).orElseThrow();
			this.info = ModList.get().getMods().stream()
					.filter(modInfo -> Objects.equals(modInfo.getModId(), modId))
					.findAny()
					.orElseThrow();
		}

		@Override
		public String getId() {
			return info.getModId();
		}

		@Override
		public String getDisplayName() {
			return info.getDisplayName();
		}

		@Override
		public String getDescription() {
			return info.getDescription();
		}

		@Override
		public List<String> getAuthors() {
			Optional<String> optional = info.getConfig().getConfigElement("authors").map(String::valueOf);
			return optional.map(List::of).orElse(Collections.emptyList());
		}

		@Override
		public String getVersion() {
			return info.getVersion().toString();
		}
	}
}
