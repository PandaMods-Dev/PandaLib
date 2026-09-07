package dev.pandasystems.pandalib.core.utils

import java.util.ServiceLoader

inline fun <reified T> loadService(): T {
	val clazz = T::class.java
	return ServiceLoader.load(clazz, clazz.classLoader).findFirst().orElseThrow()
}

inline fun <reified T> loadServiceOrNull(): T? {
	val clazz = T::class.java
    return ServiceLoader.load(clazz, clazz.classLoader).findFirst().orElse(null)
}