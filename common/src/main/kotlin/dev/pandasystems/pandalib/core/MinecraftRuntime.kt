package dev.pandasystems.pandalib.core

import dev.pandasystems.pandalib.core.utils.loadService

interface MinecraftRuntime {
	val type: RuntimeType
	val environment: RuntimeEnvironment

	companion object : MinecraftRuntime by loadService()
}

enum class RuntimeType {
	FABRIC,
	NEO_FORGE
}

enum class RuntimeEnvironment {
	CLIENT,
	SERVER,
}