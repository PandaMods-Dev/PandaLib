package dev.pandasystems.pandalib.networking

import net.minecraft.world.entity.player.Player

interface PacketSender {
    fun <T> sendToServer(type: PacketType<T>, value: T)

    fun <T> sendToPeer(peer: Player, type: PacketType<T>, value: T)

    fun <T> broadcast(
        type: PacketType<T>,
        value: T,
        filter: (Player) -> Boolean = { true },
    )
}