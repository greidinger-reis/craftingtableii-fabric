package net.johnpgr.craftingtableiifabric.api.network.packet

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

object CraftingTableIIC2SPacket : ServerPlayNetworking.PlayChannelHandler {
    override fun receive(
        server: MinecraftServer,
        playerEntity: ServerPlayerEntity,
        handler: ServerPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender
    ) {
        println("Received packet from client, player: ${playerEntity.name}")
    }
}