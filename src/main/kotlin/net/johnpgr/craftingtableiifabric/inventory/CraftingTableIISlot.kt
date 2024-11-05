package net.johnpgr.craftingtableiifabric.inventory

import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.screen.slot.Slot

class CraftingTableIISlot(
    inventory: CraftingTableIIInventory,
    index: Int,
    x: Int,
    y: Int,
) : Slot(
    inventory, index, x, y
) {
    override fun canInsert(stack: ItemStack): Boolean {
        return false
    }

    val recipe
        get(): Recipe<*>? = (inventory as CraftingTableIIInventory).recipes.getOrNull(index)
}
