@file:Suppress("UNCHECKED_CAST")

package net.johnpgr.craftingtableiifabric.api.network.packet

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
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
        val packet = CraftingPacket.read(buf, server.recipeManager) ?: return
        val inventory = playerEntity.inventory
        try {
            server.execute {
                val recipe = packet.recipe
                val items = recipe.getOutput(server.registryManager)
                val ingredients =
                    recipe.ingredients.map { it.matchingStacks.first() }

                ingredients.forEach {
                    val i = inventory.indexOf(it)
                    val amt = it.count

                    inventory.removeStack(i, amt)
                }

                playerEntity.currentScreenHandler.cursorStack = items
            }
        } catch (e: Exception) {
            CraftingTableIIFabric.LOGGER.error("Failed to craft item")
        }
    }
}