package net.johnpgr.craftingtableiifabric.inventories

import net.minecraft.inventory.Inventory

interface InventoryCraftingTableII : Inventory {
    companion object {
        const val ROWS = 5
        const val COLS = 8
        const val INVENTORY_SIZE = ROWS * COLS
    }
}