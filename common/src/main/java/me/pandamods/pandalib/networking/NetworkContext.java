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

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.world.entity.player.Player;

public class NetworkContext {
	private final Player player;

	public NetworkContext(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public Env getEnvironment() {
		return Platform.getEnvironment();
	}
}
