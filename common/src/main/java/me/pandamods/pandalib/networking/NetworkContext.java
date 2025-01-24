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

import dev.architectury.utils.Env;
import net.minecraft.world.entity.player.Player;

public class NetworkContext {
	private final Player player;
	private final Env envDirection;

	public NetworkContext(Player player, Env envDirection) {
		this.player = player;
		this.envDirection = envDirection;
	}

	/**
	 * @return The Sender if this was sent to the server or the receiver if this was sent to a client
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the environment direction the packet was send.
	 */
	public Env getDirection() {
		return envDirection;
	}
}
