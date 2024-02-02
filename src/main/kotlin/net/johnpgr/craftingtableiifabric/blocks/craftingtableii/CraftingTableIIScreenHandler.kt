package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.johnpgr.craftingtableiifabric.api.network.ModMessages
import net.johnpgr.craftingtableiifabric.api.network.packet.CraftingPacket
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.johnpgr.craftingtableiifabric.utils.RecipeHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

class CraftingTableIIScreenHandler(
    syncId: Int,
    player: PlayerEntity,
    val entity: CraftingTableIIBlockEntity,
) : ScreenHandler(
    ModBlocks.getContainerInfo(ModBlocks.CRAFTING_TABLE_II)?.handlerType,
    syncId
) {
    companion object {
        const val ROWS = 5
        const val COLS = 8
        const val INVENTORY_SIZE = ROWS * COLS
    }

    lateinit var recipeHandler: RecipeHandler

    val inventory =
        object : Inventory {
            override fun size(): Int {
                return entity.size()
            }

            override fun isEmpty(): Boolean {
                return entity.isEmpty
            }

            override fun getStack(slot: Int): ItemStack {
                return entity.getStack(slot)
            }

            override fun removeStack(slot: Int): ItemStack {
                return ItemStack.EMPTY
            }

            override fun removeStack(slot: Int, amount: Int): ItemStack {
                if(player.world.isClient) {
                    val item = getStack(slot)
                    val recipe = recipeHandler.getRecipe(item)
                    val buf = PacketByteBufs.create()

                    if (recipe != null && buf != null) {
                        CraftingPacket(recipe).write(buf)
                        ClientPlayNetworking.send(
                            ModMessages.CTII_CRAFT_RECIPE,
                            buf
                        )
                    }
                }
                return ItemStack.EMPTY
            }

            override fun setStack(slot: Int, stack: ItemStack) {
                entity.setStack(slot, stack)
                onContentChanged(this)
            }

            override fun markDirty() {
                entity.markDirty()
            }

            override fun canPlayerUse(player: PlayerEntity?): Boolean {
                return entity.canPlayerUse(player)
            }

            override fun clear() {
                entity.clear()
            }
        }

    init {

        checkSize(inventory, INVENTORY_SIZE)
        inventory.onOpen(player)

        //Our inventory
        for (row in 0..<ROWS) {
            for (col in 0..<COLS) {
                val i = col + row * COLS

                addSlot(
                    CraftingTableIISlot(
                        inventory,
                        i,
                        8 + col * 18,
                        18 + row * 18
                    )
                )
            }
        }
        //The player inventory
        for (row in 0..2) {
            for (col in 0..8) {
                addSlot(
                    Slot(
                        player.inventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        125 + row * 18
                    )
                )
            }
        }
        //The player hotbar
        for (row in 0..8) {
            addSlot(
                Slot(
                    player.inventory,
                    row,
                    8 + row * 18,
                    184
                )
            )
        }

        if (player.world.isClient) {
            val inventory = player.inventory
            val registryManager = player.world.registryManager
            val recipeBook = (player as ClientPlayerEntity).recipeBook
            recipeHandler =
                RecipeHandler(inventory, recipeBook, registryManager)

            updateRecipes()
        }
    }

    private fun updateRecipes() {
        Thread {
            Thread.sleep(64)
            recipeHandler.getOutputResults().forEach { recipe ->
                setStack(recipe)
            }
        }.start()
    }


    /**
     * setStack will set the item stack to the first empty slot in the inventory
     */
    fun setStack(stack: ItemStack) {
        for (i in 0 until inventory.size()) {
            if (inventory.getStack(i).isEmpty) {
                inventory.setStack(i, stack)
                return
            }
        }
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return true
    }

    override fun quickMove(
        playerEntity: PlayerEntity,
        invSlot: Int
    ): ItemStack {
        return ItemStack.EMPTY
    }
}