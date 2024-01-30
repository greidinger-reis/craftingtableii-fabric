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
import net.minecraft.item.Item
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
    private val inventory = DefaultedList.ofSize(
        InventoryCraftingTableII.INVENTORY_SIZE,
        ItemStack.EMPTY
    )
    private var screenHandler: ScreenHandler? = null

    override fun createMenu(
        syncId: Int,
        playerInventory: PlayerInventory,
        player: PlayerEntity
    ): ScreenHandler {
        val screenHandler = CraftingTableIIScreenHandler(syncId, playerInventory, this)
        this.screenHandler = screenHandler
        return screenHandler
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
        markDirty()
        if(screenHandler != null) {
            println("screenHandler: $screenHandler")

            screenHandler!!.sendContentUpdates()
        }

        println("items inserted: $inventory")
    }

    override fun size(): Int {
        return inventory.size
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

    override fun removeStack(slot: Int, count: Int): ItemStack {
        val result = Inventories.splitStack(inventory, slot, count)

        if (!result.isEmpty) {
            markDirty()
        }

        return result
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

    override fun canPlayerUse(player: PlayerEntity): Boolean {
        return true
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

    override fun onOpen(player: PlayerEntity) {
        super.onOpen(player)
    }
}
