package dev.pandasystems.pandalib.fabric

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.core.MinecraftRuntime
import dev.pandasystems.pandalib.core.RuntimeEnvironment
import dev.pandasystems.pandalib.core.RuntimeType
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader

@AutoService(MinecraftRuntime::class)
class FabricRuntime : MinecraftRuntime {
	override val type: RuntimeType = RuntimeType.FABRIC
	override val environment: RuntimeEnvironment = when (FabricLoader.getInstance().environmentType) {
		EnvType.CLIENT -> RuntimeEnvironment.CLIENT
		EnvType.SERVER -> RuntimeEnvironment.SERVER
	}
}