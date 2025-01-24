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

package me.pandamods.test.config;

import me.pandamods.pandalib.config.ConfigData;

public class TestConfig implements ConfigData {
	public String aString = "Hello World!";
	public float aFloat = 1.0f;
	public int anInt = 1;
	public boolean aBoolean = true;
}
