package dev.pandasystems.pandalib.fabric

import dev.pandasystems.pandalib.core.PandaLibMain
import dev.pandasystems.pandalib.fabric.networking.FabricNetworkManager
import net.fabricmc.api.ModInitializer

internal class FabricInit : ModInitializer {
    override fun onInitialize() {
        PandaLibMain()
    }
}