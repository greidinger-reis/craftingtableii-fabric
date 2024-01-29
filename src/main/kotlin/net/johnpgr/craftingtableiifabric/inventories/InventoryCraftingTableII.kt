package net.johnpgr.craftingtableiifabric.inventories

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

interface InventoryCraftingTableII : Inventory {
    companion object {
        const val inventoryRowSize = 5
        const val inventoryColSize = 8
        const val inventorySize = inventoryRowSize * inventoryColSize
    }

    fun getItems(): DefaultedList<ItemStack>

    override fun size(): Int {
        return getItems().size
    }

    override fun isEmpty(): Boolean {
        for (i in 0 until size()) {
            val stack = getStack(i);
            if (!stack.isEmpty) {
                return false;
            }
        }
        return true;
    }

    override fun getStack(slot: Int): ItemStack {
        return getItems()[slot]
    }

    override fun removeStack(slot: Int, count: Int): ItemStack {
        val result = Inventories.splitStack(getItems(), slot, count)

        if (!result.isEmpty) {
            markDirty()
        }

        return result
    }

    override fun removeStack(slot: Int): ItemStack {
        return Inventories.removeStack(getItems(), slot)
    }

    override fun setStack(slot: Int, stack: ItemStack) {
        if(stack.count > stack.maxCount) {
            stack.count = stack.maxCount
        }

        getItems()[slot] = stack
    }

    override fun canTransferTo(
        hopperInventory: Inventory,
        slot: Int,
        stack: ItemStack
    ): Boolean {
        return false
    }

    override fun isValid(slot: Int, stack: ItemStack): Boolean {
        return false
    }

    override fun clear() {
        getItems().clear()
    }

    override fun canPlayerUse(player: PlayerEntity): Boolean {
        return true
    }
}