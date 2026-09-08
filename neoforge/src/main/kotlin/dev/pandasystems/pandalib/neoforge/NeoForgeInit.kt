package dev.pandasystems.pandalib.neoforge

import dev.pandasystems.pandalib.core.PandaLibMain
import dev.pandasystems.pandalib.core.modId
import dev.pandasystems.pandalib.core.utils.loadService
import dev.pandasystems.pandalib.neoforge.networking.NeoForgeNetworkManager
import dev.pandasystems.pandalib.networking.NetworkManager
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod

@Mod(modId)
internal class NeoForgeInit(
    eventBus: IEventBus
) {
    init {
        PandaLibMain()

        val networkManager = loadService<NetworkManager>() as NeoForgeNetworkManager
        eventBus.addListener(networkManager::registrationEvent)
    }
}