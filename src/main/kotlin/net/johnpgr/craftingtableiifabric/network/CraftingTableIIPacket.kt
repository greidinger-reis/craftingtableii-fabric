package net.johnpgr.craftingtableiifabric.network

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.NetworkRecipeId
import net.minecraft.recipe.RecipeEntry
import net.minecraft.server.world.ServerWorld

data class CraftingTableIIPacket(
    val recipe: NetworkRecipeId, val syncId: Int, val quickCraft: Boolean
) : CustomPayload {
    override fun getId() = ID

    companion object {
        val ID = CustomPayload.Id<CraftingTableIIPacket>(CraftingTableIIMod.id("craft_packet"))
        val PACKET_CODEC = PacketCodec.tuple(
            NetworkRecipeId.PACKET_CODEC, CraftingTableIIPacket::recipe,
            PacketCodecs.INTEGER, CraftingTableIIPacket::syncId,
            PacketCodecs.BOOL, CraftingTableIIPacket::quickCraft,
            ::CraftingTableIIPacket,
        )

        @Suppress("UNCHECKED_CAST")
        fun register() {
            PayloadTypeRegistry.playC2S().register(ID, PACKET_CODEC)

            ServerPlayNetworking.registerGlobalReceiver(ID) { data, context ->
                val player = context.player()
                val server = player.server
                val creative = player.isCreative
                val world = player.world as ServerWorld
                val playerInventory = player.inventory

                if (player.currentScreenHandler.syncId == data.syncId && player.currentScreenHandler is CraftingTableIIScreenHandler) {
                    val craftingScreenHandler = player.currentScreenHandler as CraftingTableIIScreenHandler

                    val recipe = (server.recipeManager.get(data.recipe)?.parent
                        ?: return@registerGlobalReceiver) as? RecipeEntry<CraftingRecipe>
                        ?: return@registerGlobalReceiver

                    craftingScreenHandler.fillInputSlots(data.quickCraft, creative, recipe, world, playerInventory)

                    while (recipe.value.matches(craftingScreenHandler.input.createRecipeInput(), player.world)) {
                        val cursor = craftingScreenHandler.cursorStack
                        val output =
                            recipe.value.craft(craftingScreenHandler.input.createRecipeInput(), server.registryManager)

                        craftingScreenHandler.updateResultSlot(output)

                        // This will not take the item stack, just update the input inventory
                        val resultSlot = craftingScreenHandler.getSlot(CraftingTableIIScreenHandler.RESULT_INDEX)!!
                        resultSlot.onTakeItem(player, output)

                        when {
                            cursor.isEmpty -> {
                                player.currentScreenHandler.cursorStack = output
                            }

                            cursor.item == output.item && cursor.isStackable && cursor.count + output.count <= cursor.maxCount -> {
                                cursor.increment(output.count)
                            }

                            else -> {
                                player.inventory.offerOrDrop(output)
                            }
                        }
                    }
                    craftingScreenHandler.clearCraftingSlots()
                }
            }
        }
    }
}

