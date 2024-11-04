package net.johnpgr.craftingtableiifabric.network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeEntry
import net.minecraft.util.Identifier
import kotlin.jvm.optionals.getOrNull

class CraftingTableIIPacket(
    val recipe: Identifier, val syncId: Int, val quickCraft: Boolean
) {
    companion object {
        val ID = CraftingTableIIMod.id("craft_packet")

        @Suppress("UNCHECKED_CAST")
        fun register() {
            ServerPlayNetworking.registerGlobalReceiver(ID) { server, player, handler, buf, responseSender ->
                val data = read(buf)
                if (player.currentScreenHandler.syncId == data.syncId && player.currentScreenHandler is CraftingTableIIScreenHandler) {
                    val craftingScreenHandler = player.currentScreenHandler as CraftingTableIIScreenHandler

                    val res = server.recipeManager.get(data.recipe)
                    val recipe = (res.getOrNull() ?: return@registerGlobalReceiver) as RecipeEntry<Recipe<RecipeInputInventory>>

                    craftingScreenHandler.fillInputSlots(
                        data.quickCraft, recipe, player
                    )

                    while (recipe.value.matches(
                            craftingScreenHandler.input, player.world
                        )
                    ) {
                        val cursor = craftingScreenHandler.cursorStack
                        val output = recipe.value.craft(
                            craftingScreenHandler.input, server.registryManager
                        )

                        craftingScreenHandler.updateResultSlot(output)

                        // This will not take the item stack, just update the input inventory
                        val resultSlot = craftingScreenHandler.getSlot(craftingScreenHandler.craftingResultSlotIndex)!!
                        resultSlot.onTakeItem(
                            player, output
                        )

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

        fun read(
            buf: PacketByteBuf,
        ): CraftingTableIIPacket {
            val recipe = buf.readIdentifier()
            val syncId = buf.readInt()
            val quickCraft = buf.readBoolean()

            return CraftingTableIIPacket(recipe, syncId, quickCraft)
        }
    }

    fun toBuf(): PacketByteBuf {
        val buf = PacketByteBufs.create()
        buf.writeIdentifier(recipe)
        buf.writeInt(syncId)
        buf.writeBoolean(quickCraft)
        return buf
    }

    fun send() {
        ClientPlayNetworking.send(ID, toBuf())
    }
}

