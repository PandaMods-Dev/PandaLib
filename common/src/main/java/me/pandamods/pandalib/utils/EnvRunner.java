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

package me.pandamods.pandalib.utils;

import dev.architectury.utils.Env;
import me.pandamods.pandalib.platform.Services;

import java.util.function.Supplier;

public class EnvRunner {
	public static void runIf(Env env, Supplier<Runnable> task) {
		if (Services.GAME.getEnvironment() == env)
			task.get().run();
	}
}
