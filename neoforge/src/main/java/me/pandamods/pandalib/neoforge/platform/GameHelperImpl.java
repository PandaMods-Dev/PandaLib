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

import dev.architectury.utils.Env;
import me.pandamods.pandalib.platform.services.GameHelper;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class GameHelperImpl implements GameHelper {
	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.isProduction();
	}

	@Override
	public boolean isProductionEnvironment() {
		return FMLLoader.isProduction();
	}

	@Override
	public Env getEnvironment() {
		return switch (FMLLoader.getDist()) {
			case CLIENT -> Env.CLIENT;
			case DEDICATED_SERVER -> Env.SERVER;
		};
	}

	@Override
	public boolean isClient() {
		return FMLLoader.getDist().isClient();
	}

	@Override
	public boolean isServer() {
		return FMLLoader.getDist().isDedicatedServer();
	}

	@Override
	public Path getGameDir() {
		return FMLPaths.GAMEDIR.get();
	}

	@Override
	public Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	@Override
	public Path getModDir() {
		return FMLPaths.MODSDIR.get();
	}
}
