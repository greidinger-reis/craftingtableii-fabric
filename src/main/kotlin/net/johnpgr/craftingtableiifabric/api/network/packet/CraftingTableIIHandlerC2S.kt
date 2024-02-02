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
        val inventory = playerEntity.inventory
        server.execute {
            val recipe = packet.recipe
            val items = recipe.getOutput(server.registryManager).copy()

            var toConsume = recipe.ingredients.size

            for(ingredient in recipe.ingredients) {
                if (toConsume == 0) break
                val stacks = ingredient.matchingStacks
                for(stack in stacks){
                    val index = inventory.indexOf(stack)
                    if (index != -1) {
                        val amt = stack.count
                        inventory.removeStack(index, amt)
                        toConsume -= amt
                    }
                }
            }

            val cursorStack = playerEntity.currentScreenHandler.cursorStack
            if (cursorStack.isEmpty) {
                playerEntity.currentScreenHandler.cursorStack = items
            } else if (cursorStack.item == items.item
                && cursorStack.isStackable
                && cursorStack.count + items.count <= cursorStack.maxCount) {
                cursorStack.increment(
                    items.count
                )
            } else {
                playerEntity.inventory.offerOrDrop(items)
            }
        }
    }
}