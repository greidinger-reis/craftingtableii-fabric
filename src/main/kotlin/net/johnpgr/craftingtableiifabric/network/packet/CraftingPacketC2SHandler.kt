@file:Suppress("UNCHECKED_CAST")

package net.johnpgr.craftingtableiifabric.network.packet

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIScreenHandler
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Recipe
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

object CraftingPacketC2SHandler : ServerPlayNetworking.PlayChannelHandler {
    override fun receive(
        server: MinecraftServer,
        player: ServerPlayerEntity,
        handler: ServerPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender,
    ) {
        val packet = CraftingPacket.read(buf)

        if (player.currentScreenHandler.syncId == packet.syncId
            && player.currentScreenHandler is CraftingTableIIScreenHandler
        ) {
            val craftingScreenHandler =
                player.currentScreenHandler as CraftingTableIIScreenHandler

            server.recipeManager.get(packet.recipe)
                .ifPresent { recipe ->
                    craftingScreenHandler.fillInputSlots(
                        packet.quickCraft,
                        recipe,
                        player
                    )

                    while ((recipe as Recipe<RecipeInputInventory>).matches(
                            craftingScreenHandler.input,
                            player.world
                        )
                    ) {
                        val cursor = craftingScreenHandler.cursorStack
                        val output = recipe
                            .craft(
                                craftingScreenHandler.input,
                                server.registryManager
                            )

                        craftingScreenHandler.updateResultSlot(output)

                        // This will not take the item stack, just update the input inventory
                        (craftingScreenHandler.getSlot(craftingScreenHandler.craftingResultSlotIndex) as CraftingResultSlot)
                            .onTakeItem(player, output)

                        if (cursor.isEmpty) {
                            player.currentScreenHandler.cursorStack = output
                        } else if (cursor.item == output.item
                            && cursor.isStackable
                            && cursor.count + output.count <= cursor.maxCount
                        ) {
                            cursor.increment(
                                output.count
                            )
                        } else {
                            player.inventory.offerOrDrop(output)
                        }
                    }
                    craftingScreenHandler.clearCraftingSlots()
                }
        }
    }
}