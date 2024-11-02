package net.johnpgr.craftingtableiifabric.inventory

import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

class CraftingTableIIInventory(
    val entity: CraftingTableIIEntity,
) : Inventory {
    companion object {
        const val COLS = 8
        const val ROWS = 5
        const val SIZE = ROWS * COLS
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

    override fun removeStack(slot: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun setStack(slot: Int, stack: ItemStack) {
        if (stack.isEmpty) return

        entity.setStack(slot, stack)
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