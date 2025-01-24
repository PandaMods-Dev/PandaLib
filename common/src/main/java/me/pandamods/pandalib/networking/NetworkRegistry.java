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

package me.pandamods.pandalib.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public interface NetworkingRegistry {
	@Environment(EnvType.CLIENT)
	void registerClientReceiver(ResourceLocation resourceLocation,
								NetworkReceiver receiver);

	void registerServerReceiver(ResourceLocation resourceLocation,
								NetworkReceiver receiver);

	void registerBiDirectionalReceiver(ResourceLocation resourceLocation,
									   NetworkReceiver clientReceiver,
									   NetworkReceiver serverReceiver);
}
