package dev.pandasystems.pandalib.fabric.networking

import com.google.auto.service.AutoService
import dev.pandasystems.pandalib.core.MinecraftRuntime
import dev.pandasystems.pandalib.core.RuntimeEnvironment
import dev.pandasystems.pandalib.core.handles.player.PlayerHandle
import dev.pandasystems.pandalib.core.handles.player.handle
import dev.pandasystems.pandalib.core.lifecycles.ServerLifecycle
import dev.pandasystems.pandalib.networking.*
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

@AutoService(NetworkManager::class)
class FabricNetworkManager : NetworkManager {
    private val packetTypes = mutableMapOf<PacketId, PacketType<*>>()

    private val server: MinecraftServer? get() = ServerLifecycle.serverInstance

    override fun <T> sendToServer(type: PacketType<T>, value: T) {
        checkRegistered(type, PacketDirection.CLIENT_TO_SERVER)
        check(MinecraftRuntime.environment == RuntimeEnvironment.CLIENT) {
            "sendToServer can only be called on a client."
        }
        ClientPlayNetworking.send(payload(type, value))
    }

    override fun <T> sendToPeer(peer: PlayerHandle, type: PacketType<T>, value: T) {
        checkRegistered(type, PacketDirection.SERVER_TO_CLIENT)
        ServerPlayNetworking.send(peer.resolve() as ServerPlayer, payload(type, value))
    }

    override fun <T> broadcast(
        type: PacketType<T>,
        value: T,
        filter: (PlayerHandle) -> Boolean,
    ) {
        checkRegistered(type, PacketDirection.SERVER_TO_CLIENT)
        val currentServer = checkNotNull(server) {
            "broadcast can only be called while a Minecraft server is running."
        }
        currentServer.playerList.players.forEach { player ->
            val peer = player.handle()
            if (filter(peer)) ServerPlayNetworking.send(player, payload(type, value))
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

    private fun <T> payload(type: PacketType<T>, value: T): FabricPacketPayload =
        FabricPacketPayload(FabricPacketPayload.type(type.id.toIdentifier()), type.codec.encode(value))

    override fun <T> register(
        type: PacketType<T>,
        handler: PacketHandler<T>
    ) {
        require(type.id !in packetTypes) {
            "A packet is already registered with id '${type.id}'."
        }

        val payloadType = FabricPacketPayload.type(type.id.toIdentifier())
        val payloadCodec = FabricPacketPayload.codec(payloadType)
        when (type.direction) {
            PacketDirection.CLIENT_TO_SERVER -> {
                PayloadTypeRegistry.serverboundPlay().register(payloadType, payloadCodec)
                check(ServerPlayNetworking.registerGlobalReceiver(payloadType) { payload, context ->
                    handler.handle(
                        PacketContextImpl(
                            peer = context.player().handle(),
                            executor = { task -> context.server().execute(task) },
                            sender = this,
                            replyToServer = false,
                        ),
                        type.codec.decode(payload.data),
                    )
                }) {
                    "Fabric already has a serverbound receiver for packet '${type.id}'."
                }
            }

            PacketDirection.SERVER_TO_CLIENT -> {
                PayloadTypeRegistry.clientboundPlay().register(payloadType, payloadCodec)
                if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
                    check(ClientPlayNetworking.registerGlobalReceiver(payloadType) { payload, context ->
                        handler.handle(
                            PacketContextImpl(
                                peer = context.player().handle(),
                                executor = { task -> context.client().execute(task) },
                                sender = this,
                                replyToServer = true,
                            ),
                            type.codec.decode(payload.data),
                        )
                    }) {
                        "Fabric already has a clientbound receiver for packet '${type.id}'."
                    }
                }
            }
        }
        packetTypes[type.id] = type
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