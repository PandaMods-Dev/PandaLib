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

package me.pandamods.pandalib.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class ClassUtils {
	public static <T> T constructUnsafely(Class<T> cls) {
		try {
			Constructor<T> constructor = cls.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void setFieldUnsafely(Object parentObject, Field field, T value) {
		try {
			field.set(parentObject, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFieldUnsafely(Object parentObject, Field field) {
		try {
			return (T) field.get(parentObject);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean doesClassExist(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
