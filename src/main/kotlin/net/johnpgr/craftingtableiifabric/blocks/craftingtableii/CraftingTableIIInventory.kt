package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

class CraftingTableIIInventory(
    val entity: CraftingTableIIBlockEntity,
    val handler: CraftingTableIIScreenHandler
) : Inventory {
    companion object {
        const val COLS = 8
        const val ROWS = 5
        const val SIZE = ROWS * COLS

        fun list(): DefaultedList<ItemStack> {
            return DefaultedList.ofSize(SIZE * 25, ItemStack.EMPTY)
        }
    }

    //return the size of the inventory without empty slots
    val sizeNonEmpty
        get():Int {
            var size = 0

            for (i in 0 until entity.size()) {
                if (!entity.getStack(i).isEmpty) {
                    size++
                }
            }

            return size
        }


    override fun size(): Int {
        return entity.size()
    }

    override fun isEmpty(): Boolean {
        return entity.isEmpty
    }

    override fun getStack(slot: Int): ItemStack {
        return entity.getStack(slot)
    }

    fun getStackOptional(slot: Int): ItemStack? {
        return if (slot in 0..this.size()) entity.getStack(slot) else null
    }

    override fun removeStack(slot: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun setStack(slot: Int, stack: ItemStack) {
        if (stack.isEmpty) return

        entity.setStack(slot, stack)
        handler.onContentChanged(this)
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

    fun addRecipeItem(stack: ItemStack) {
        for (i in 0 until this.size()) {
            if (this.getStack(i).isEmpty) {
                this.setStack(i, stack)
                return
            }
        }
    }
}