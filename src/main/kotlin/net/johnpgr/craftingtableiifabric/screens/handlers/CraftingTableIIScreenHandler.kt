package net.johnpgr.craftingtableiifabric.screens.handlers

import net.johnpgr.craftingtableiifabric.inventories.InventoryCraftingTableII
import net.johnpgr.craftingtableiifabric.inventories.slots.CraftingTableIISlot
import net.johnpgr.craftingtableiifabric.screens.ModScreens
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot

class CraftingTableIIScreenHandler : ScreenHandler {
    private val inventory: Inventory

    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
    ) : this(
        syncId,
        playerInventory,
        SimpleInventory(InventoryCraftingTableII.INVENTORY_SIZE),
    )

    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
        inventory: Inventory,
    ) : super(ModScreens.CRAFTING_TABLE_II_SCREEN_HANDLER, syncId) {
        checkSize(inventory, InventoryCraftingTableII.INVENTORY_SIZE)
        this.inventory = inventory
        inventory.onOpen(playerInventory.player)

        //Our inventory
        for (row in 0..<InventoryCraftingTableII.ROWS) {
            for (col in 0..<InventoryCraftingTableII.COLS) {
                addSlot(
                    CraftingTableIISlot(
                        inventory,
                        col + row * InventoryCraftingTableII.COLS,
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

    override fun canUse(player: PlayerEntity): Boolean {
        return inventory.canPlayerUse(player)
    }

    override fun onClosed(player: PlayerEntity) {
        super.onClosed(player)
        this.inventory.onClose(player)
    }

    override fun quickMove(
        playerEntity: PlayerEntity,
        invSlot: Int
    ): ItemStack {
        return ItemStack.EMPTY
    }
}