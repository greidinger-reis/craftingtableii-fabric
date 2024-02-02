package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

class CraftingTableIIBlockEntity(
    craftingTableII: CraftingTableIIBlock,
    pos: BlockPos,
    state: BlockState,
) :
    BlockEntity(ModBlocks.getEntityType(craftingTableII), pos, state),
    Inventory {
    private var inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(
        CraftingTableIIScreenHandler.INVENTORY_SIZE,
        ItemStack.EMPTY
    )

    override fun readNbt(tag: NbtCompound) {
        super.readNbt(tag)
        Inventories.readNbt(tag, inventory)
    }

    override fun writeNbt(tag: NbtCompound) {
        super.writeNbt(tag)
        Inventories.writeNbt(tag, this.inventory)
    }

    override fun size(): Int {
        return inventory.size
    }

    override fun isEmpty(): Boolean {
        return inventory.all { it.isEmpty }
    }

    override fun getStack(slot: Int): ItemStack {
        return inventory[slot]
    }

    override fun setStack(slot: Int, stack: ItemStack) {
        if (stack.count > stack.maxCount) {
            stack.count = stack.maxCount
        }

        inventory[slot] = stack
    }

    override fun removeStack(slot: Int, count: Int): ItemStack {
        return Inventories.splitStack(inventory, slot, count)
    }

    override fun removeStack(slot: Int): ItemStack {
        return Inventories.removeStack(inventory, slot)
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
        inventory.clear()
    }

    override fun canPlayerUse(player: PlayerEntity?): Boolean {
        return true
    }
}
