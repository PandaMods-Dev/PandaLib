/*
 * Copyright (C) 2024 Oliver Froberg (The Panda Oliver)
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 * You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.pandamods.pandalib.neoforge;

import me.pandamods.pandalib.PandaLib;
import me.pandamods.pandalib.neoforge.client.PandaLibClientNeoForge;
import me.pandamods.pandalib.neoforge.event.EventHandlerImpl;
import me.pandamods.pandalib.platform.Services;
import net.neoforged.fml.common.Mod;

@Mod(PandaLib.MOD_ID)
public class PandaLibNeoForge {
    public PandaLibNeoForge() {
		PandaLib.init();

		EventHandlerImpl.register();

		#if MC_VER < MC_1_21
		if (Services.PLATFORM.getGame().isClient())
			new PandaLibClientNeoForge();
		#endif
    }
}
