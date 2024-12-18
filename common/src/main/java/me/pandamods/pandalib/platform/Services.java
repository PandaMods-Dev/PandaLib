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

package me.pandamods.pandalib.platform;

import com.mojang.logging.LogUtils;
import me.pandamods.pandalib.platform.services.INetworkHelper;
import org.slf4j.Logger;

import java.util.ServiceLoader;

public class Services {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static final INetworkHelper NETWORK_HELPER = load(INetworkHelper.class);

	private static <T> T load(Class<T> serviceClass) {
		final T loadedService = ServiceLoader.load(serviceClass).findFirst().orElseThrow(() ->
				new NullPointerException("Failed to load service for " + serviceClass.getName()));
		LOGGER.debug("Loaded {} for service {}", loadedService, serviceClass);
		return loadedService;
	}
}
