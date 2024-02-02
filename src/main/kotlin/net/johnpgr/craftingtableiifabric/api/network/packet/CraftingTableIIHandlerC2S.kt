@file:Suppress("UNCHECKED_CAST")

package net.johnpgr.craftingtableiifabric.api.network.packet

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

object CraftingTableIIHandlerC2S : ServerPlayNetworking.PlayChannelHandler {
    override fun receive(
        server: MinecraftServer,
        playerEntity: ServerPlayerEntity,
        handler: ServerPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender
    ) {
        val packet = CraftingPacket.read(buf, server.recipeManager) ?: return
        val playerInventory = playerEntity.inventory

        server.execute {
            val recipe = packet.recipe
            val itemStack = recipe.getOutput(server.registryManager).copy()

            var toConsume = recipe.ingredients.size

            for(ingredient in recipe.ingredients) {
                if (toConsume == 0) break
                val stacks = ingredient.matchingStacks
                for(stack in stacks){
                    val index = playerInventory.indexOf(stack)
                    if (index != -1) {
                        val amt = stack.count
                        playerInventory.removeStack(index, amt)
                        toConsume -= amt
                    }
                }
            }

            val cursorStack = playerEntity.currentScreenHandler.cursorStack
            if (cursorStack.isEmpty) {
                playerEntity.currentScreenHandler.cursorStack = itemStack
            } else if (cursorStack.item == itemStack.item
                && cursorStack.isStackable
                && cursorStack.count + itemStack.count <= cursorStack.maxCount) {
                cursorStack.increment(
                    itemStack.count
                )
            } else {
                playerEntity.inventory.offerOrDrop(itemStack)
            }
        }
    }
}