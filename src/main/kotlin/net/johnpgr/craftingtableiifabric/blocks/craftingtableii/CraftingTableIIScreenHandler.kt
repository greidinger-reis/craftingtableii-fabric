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

    private lateinit var recipeHandler: RecipeHandler

    private val inventory =
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
                return ItemStack.EMPTY
            }

            override fun setStack(slot: Int, stack: ItemStack) {
                if(stack.isEmpty) return

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
        checkSize(this.inventory, INVENTORY_SIZE)
        inventory.onOpen(player)

        //Our inventory
        for (row in 0..<ROWS) {
            for (col in 0..<COLS) {
                val i = col + row * COLS

                this.addSlot(
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
                this.addSlot(
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
            this.addSlot(
                Slot(
                    player.inventory,
                    row,
                    8 + row * 18,
                    184
                )
            )
        }

        if (player.world.isClient) {
            val playerInventory = player.inventory
            val recipeBook = (player as ClientPlayerEntity).recipeBook
            this.recipeHandler =
                RecipeHandler(playerInventory, recipeBook)

            this.updateRecipes()

            //TODO: This is a temporary solution, we need to find a better way to update the recipes when the player inventory change
            //Maybe we will need to create a Inventory wrapper that will notify the RecipeHandler when the inventory change
            //Or maybe we will need a mixin for this
            Thread{
                var previousChangeCount = playerInventory.changeCount
                while (true) {
                    Thread.sleep(1)
                    if (previousChangeCount != playerInventory.changeCount) {
                        previousChangeCount = playerInventory.changeCount
                        this.updateRecipes()
                    }
                }
            }.start()
        }
    }

    override fun onSlotClick(
        slotIndex: Int,
        button: Int,
        actionType: SlotActionType,
        player: PlayerEntity
    ) {
        super.onSlotClick(slotIndex, button, actionType, player)
        if (player.world.isClient) {
            //check if the slot is our inventory
            if (slotIndex in 0..39) {
                val item = this.inventory.getStack(slotIndex)
                val recipe = this.recipeHandler.getRecipe(item)
                val buf = PacketByteBufs.create()

                if (recipe != null && buf != null) {
                    CraftingPacket(recipe).write(buf)
                    ClientPlayNetworking.send(
                        ModMessages.CTII_CRAFT_RECIPE,
                        buf
                    )
                }
            }
        }
    }

    private fun updateRecipes() {
        this.inventory.clear()

        this.recipeHandler.getCraftableItemStacks().forEach { stack ->
            addStack(stack)
        }
    }


    /**
     * addStack will set the item stack to the first empty slot in the inventory
     */
    private fun addStack(stack: ItemStack) {
        for (i in 0 until this.inventory.size()) {
            if (this.inventory.getStack(i).isEmpty) {
                this.inventory.setStack(i, stack)
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