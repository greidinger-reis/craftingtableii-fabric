package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.johnpgr.craftingtableiifabric.utils.SyncableBlockEntity
import net.minecraft.block.BlockState
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
    SyncableBlockEntity(ModBlocks.getEntityType(craftingTableII), pos, state),
    CraftingTableIIInventory {
    var inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(
        CraftingTableIIInventory.INVENTORY_SIZE,
        ItemStack.EMPTY
    )


    override fun readNbt(tag: NbtCompound) {
        super.readNbt(tag)
        Inventories.readNbt(tag, inventory)
    }

    override fun readClientNbt(tag: NbtCompound) {
        this.inventory = DefaultedList.ofSize(
            CraftingTableIIInventory.INVENTORY_SIZE,
            ItemStack.EMPTY
        )
        Inventories.readNbt(tag, this.inventory)
    }

    override fun writeNbt(tag: NbtCompound) {
        super.writeNbt(tag)
        Inventories.writeNbt(tag, this.inventory)
    }

    override fun writeClientNbt(tag: NbtCompound): NbtCompound {
        Inventories.writeNbt(tag, this.inventory)
        return tag
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

    fun setStacks(items: List<ItemStack>) {
        items.forEachIndexed { index, itemStack ->
            setStack(index, itemStack)
        }

        println("items inserted: $inventory")
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
