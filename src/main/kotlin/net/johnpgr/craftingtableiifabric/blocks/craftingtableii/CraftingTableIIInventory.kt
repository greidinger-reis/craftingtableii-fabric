package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.minecraft.inventory.Inventory

interface CraftingTableIIInventory : Inventory {
    companion object {
        const val ROWS = 5
        const val COLS = 8
        const val INVENTORY_SIZE = ROWS * COLS
    }
}