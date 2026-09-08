package dev.pandasystems.pandalib.neoforge

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.core.MinecraftRuntime
import dev.pandasystems.pandalib.core.RuntimeEnvironment
import dev.pandasystems.pandalib.core.RuntimeType
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.loading.FMLEnvironment

@AutoService(MinecraftRuntime::class)
class NeoForgeRuntime : MinecraftRuntime {
	override val type: RuntimeType = RuntimeType.NEO_FORGE
	override val environment: RuntimeEnvironment = when (FMLEnvironment.getDist()) {
		Dist.CLIENT -> RuntimeEnvironment.CLIENT
		Dist.DEDICATED_SERVER -> RuntimeEnvironment.SERVER
	}
}