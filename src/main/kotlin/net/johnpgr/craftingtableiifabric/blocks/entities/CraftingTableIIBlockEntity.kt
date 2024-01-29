package net.johnpgr.craftingtableiifabric.blocks.entities

import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.johnpgr.craftingtableiifabric.inventories.InventoryCraftingTableII
import net.johnpgr.craftingtableiifabric.screens.handlers.CraftingTableIIScreenHandler
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

class CraftingTableIIBlockEntity(
    pos: BlockPos,
    state: BlockState,
) :
    BlockEntity(ModBlocks.CRAFTING_TABLE_II_ENTITY, pos, state),
    NamedScreenHandlerFactory,
    InventoryCraftingTableII {
    private val inventory = DefaultedList.ofSize<ItemStack>(
        InventoryCraftingTableII.inventorySize,
        ItemStack.EMPTY
    )

    override fun getItems(): DefaultedList<ItemStack> {
        return inventory
    }

    override fun createMenu(
        syncId: Int,
        playerInventory: PlayerInventory,
        player: PlayerEntity
    ): ScreenHandler {
        return CraftingTableIIScreenHandler(syncId, playerInventory, this)
    }

    override fun getDisplayName(): Text {
        return Text.translatable(cachedState.block.translationKey)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        Inventories.readNbt(nbt, inventory)
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        if (!this.inventory.isEmpty()) {
            Inventories.writeNbt(nbt, inventory)
        }
    }
}
