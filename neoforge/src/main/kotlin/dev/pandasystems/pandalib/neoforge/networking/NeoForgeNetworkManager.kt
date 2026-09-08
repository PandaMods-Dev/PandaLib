package dev.pandasystems.pandalib.neoforge.networking

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.core.MinecraftRuntime
import dev.pandasystems.pandalib.core.RuntimeEnvironment
import dev.pandasystems.pandalib.core.lifecycles.ServerLifecycle
import dev.pandasystems.pandalib.networking.*
import dev.pandasystems.pandalib.registry.DeferredRegistry
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

@AutoService(NetworkManager::class)
class NeoForgeNetworkManager : NetworkManager {
    private val packetTypes = mutableMapOf<PacketId, PacketType<*>>()
    private val deferredPacketTypes = DeferredRegistry<PacketType<*>, PacketHandler<*>>()

    private val server: MinecraftServer? get() = ServerLifecycle.serverInstance

    override fun <T> sendToServer(type: PacketType<T>, value: T) {
        checkRegistered(type, PacketDirection.CLIENT_TO_SERVER)
        check(MinecraftRuntime.environment == RuntimeEnvironment.CLIENT) {
            "sendToServer can only be called on a client."
        }
        ClientPacketDistributor.sendToServer(payload(type, value))
    }

    override fun <T> sendToPeer(peer: Player, type: PacketType<T>, value: T) {
        checkRegistered(type, PacketDirection.SERVER_TO_CLIENT)
        PacketDistributor.sendToPlayer(peer as ServerPlayer, payload(type, value))
    }

    override fun <T> broadcast(
        type: PacketType<T>,
        value: T,
        filter: (Player) -> Boolean,
    ) {
        checkRegistered(type, PacketDirection.SERVER_TO_CLIENT)
        val currentServer = checkNotNull(server) {
            "broadcast can only be called while a Minecraft server is running."
        }
        currentServer.playerList.players.forEach { player ->
            val peer = player
            if (filter(peer)) PacketDistributor.sendToPlayer(player, payload(type, value))
        }
    }

    private fun <T> checkRegistered(type: PacketType<T>, expectedDirection: PacketDirection) {
        require(type.direction == expectedDirection) {
            "Packet '${type.id}' has direction ${type.direction}; expected $expectedDirection."
        }
        check(packetTypes[type.id] === type) {
            "Packet '${type.id}' must be registered before it can be sent."
        }
    }

    private fun <T> payload(type: PacketType<T>, value: T): NeoForgePacketPayload =
        NeoForgePacketPayload(
            NeoForgePacketPayload.type(
                type.id.toIdentifier()
            ), type.codec.encode(value)
        )

    override fun <T> register(
        type: PacketType<T>,
        handler: PacketHandler<T>
    ) {
        require(type.id !in packetTypes) {
            "A packet is already registered with id '${type.id}'."
        }

        deferredPacketTypes.register(type) { handler }
        packetTypes[type.id] = type
    }

    @Suppress("UNCHECKED_CAST")
    internal fun registrationEvent(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")

        deferredPacketTypes.registerAll { type, factory ->
            val handler = factory() as PacketHandler<Any>

            val payloadType = NeoForgePacketPayload.type(type.id.toIdentifier())
            val payloadCodec = NeoForgePacketPayload.codec(payloadType)
            when (type.direction) {
                PacketDirection.CLIENT_TO_SERVER -> {
                    registrar.commonToClient(
                        payloadType,
                        payloadCodec as StreamCodec<FriendlyByteBuf, NeoForgePacketPayload>
                    ) { payload, context ->
                        handler.handle(
                            PacketContextImpl(
                                peer = context.player(),
                                executor = { task -> context.enqueueWork { task() } },
                                sender = this,
                                replyToServer = false
                            ),
                            type.codec.decode(payload.data) as Any
                        )
                    }
                }
                PacketDirection.SERVER_TO_CLIENT -> {
                    registrar.commonToServer(
                        payloadType,
                        payloadCodec as StreamCodec<FriendlyByteBuf, NeoForgePacketPayload>
                    ) { payload, context ->
                        handler.handle(
                            PacketContextImpl(
                                peer = context.player(),
                                executor = { task -> context.enqueueWork { task() } },
                                sender = this,
                                replyToServer = true
                            ),
                            type.codec.decode(payload.data) as Any
                        )
                    }
                }
            }

            handler
        }
    }
}

internal fun PacketId.toIdentifier(): Identifier {
    require(':' in value) {
        "Packet id '$value' must be namespaced, for example 'examplemod:sync'."
    }
    return try {
        Identifier.parse(value)
    } catch (exception: IllegalArgumentException) {
        throw IllegalArgumentException(
            "Packet id '$value' is not a valid Minecraft identifier. Use a namespaced id such as 'examplemod:sync'.",
            exception,
        )
    }
}