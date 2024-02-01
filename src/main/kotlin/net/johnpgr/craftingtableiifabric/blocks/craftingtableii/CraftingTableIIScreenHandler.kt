package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CraftingTableIIScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    val entity: CraftingTableIIBlockEntity,
    private val blockContext: ScreenHandlerContext
) : ScreenHandler(
    ModBlocks.getContainerInfo(ModBlocks.CRAFTING_TABLE_II)?.handlerType,
    syncId
) {
    companion object {
        const val ROWS = 5
        const val COLS = 8
        const val INVENTORY_SIZE = ROWS * COLS
    }

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
        inventory.onOpen(playerInventory.player)

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
                        playerInventory,
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
                    playerInventory,
                    row,
                    8 + row * 18,
                    184
                )
            )
        }
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
        return blockContext.get({ world: World, blockPos: BlockPos ->
            if (world.getBlockState(
                    blockPos
                ).block != ModBlocks.CRAFTING_TABLE_II
            ) false else player.squaredDistanceTo(
                blockPos.x + .5,
                blockPos.y + .5,
                blockPos.z + .5
            ) < 64.0
        }, true)
    }

    override fun quickMove(
        playerEntity: PlayerEntity,
        invSlot: Int
    ): ItemStack {
        return ItemStack.EMPTY
    }
}