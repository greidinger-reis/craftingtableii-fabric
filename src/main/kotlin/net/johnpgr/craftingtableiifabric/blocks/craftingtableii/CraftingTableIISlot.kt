package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.screen.slot.Slot

class CraftingTableIISlot(inventory: Inventory, index: Int, x: Int, y: Int) :
    Slot(
        inventory,
        index, x, y
    ) {
    override fun canInsert(stack: ItemStack): Boolean {
        return false
    }
}