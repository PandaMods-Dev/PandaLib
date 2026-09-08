package dev.pandasystems.pandalib.networking

import dev.pandasystems.pandalib.core.utils.loadService

interface NetworkManager : PacketSender, NetworkRegistrar {
	companion object : NetworkManager by loadService()
}