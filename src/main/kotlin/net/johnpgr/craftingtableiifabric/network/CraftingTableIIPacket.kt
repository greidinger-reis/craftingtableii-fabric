package net.johnpgr.craftingtableiifabric.network

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeEntry
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.util.Identifier

data class CraftingTableIIPacket(
    val recipe: Identifier,
    val syncId: Int,
    val quickCraft: Boolean
) : CustomPayload {
    companion object {
        val ID =
            CustomPayload.Id<CraftingTableIIPacket>(CraftingTableIIMod.id("craft_packet"))
        val PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, CraftingTableIIPacket::recipe,
            PacketCodecs.INTEGER, CraftingTableIIPacket::syncId,
            PacketCodecs.BOOL, CraftingTableIIPacket::quickCraft,
            ::CraftingTableIIPacket,
        )

        fun register() {
            PayloadTypeRegistry.playC2S().register(ID, PACKET_CODEC)

            ServerPlayNetworking.registerGlobalReceiver(ID) { data, context ->
                val player = context.player()
                val server = player.server
                if (
                    player.currentScreenHandler.syncId == data.syncId &&
                    player.currentScreenHandler is CraftingTableIIScreenHandler
                ) {
                    val craftingScreenHandler =
                        player.currentScreenHandler as CraftingTableIIScreenHandler

                    server.recipeManager.get(data.recipe)
                        .ifPresent { recipe ->
                            craftingScreenHandler.fillInputSlots(
                                data.quickCraft,
                                recipe,
                                player
                            )

                            while (
                                @Suppress("UNCHECKED_CAST")
                                (recipe as RecipeEntry<Recipe<RecipeInputInventory>>).value.matches(
                                    craftingScreenHandler.inputInventory,
                                    player.world
                                )
                            ) {
                                val cursor =
                                    craftingScreenHandler.cursorStack
                                val output = recipe.value.craft(
                                    craftingScreenHandler.inputInventory,
                                    server.registryManager
                                )

                                craftingScreenHandler.updateResultSlot(
                                    output
                                )

                                // This will not take the item stack, just update the input inventory
                                (craftingScreenHandler.getSlot(
                                    craftingScreenHandler.craftingResultSlotIndex
                                ) as CraftingResultSlot)
                                    .onTakeItem(player, output)

                                if (cursor.isEmpty) {
                                    player.currentScreenHandler.cursorStack =
                                        output
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
    }

    override fun getId() = ID
}


