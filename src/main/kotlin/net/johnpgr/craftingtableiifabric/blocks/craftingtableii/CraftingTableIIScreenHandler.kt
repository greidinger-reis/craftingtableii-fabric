package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.johnpgr.craftingtableiifabric.api.network.ModMessages
import net.johnpgr.craftingtableiifabric.api.network.packet.CraftingPacket
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.johnpgr.craftingtableiifabric.utils.RecipeManager
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.screen.AbstractRecipeScreenHandler
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

class CraftingTableIIScreenHandler(
    syncId: Int,
    private val player: PlayerEntity,
    val entity: CraftingTableIIBlockEntity,
) : AbstractRecipeScreenHandler<RecipeInputInventory>(
    ModBlocks.getContainerInfo(ModBlocks.CRAFTING_TABLE_II)?.handlerType,
    syncId,
) {
    val input = CraftingInventory(this, 3, 3)
    val result = CraftingResultInventory()

    companion object {
        const val INVENTORY_COLS = 8
        const val INVENTORY_ROWS = 5
        const val INVENTORY_SIZE = INVENTORY_ROWS * INVENTORY_COLS
    }

    private var recipeHandler: RecipeManager? = null

    private val inventory =
        object : Inventory {
            override fun size(): Int {
                return entity.size()
            }

            override fun isEmpty(): Boolean {
                return entity.isEmpty
            }

            override fun getStack(slot: Int): ItemStack {
                return entity.getStack(slot + 10)
            }

            override fun removeStack(slot: Int): ItemStack {
                return ItemStack.EMPTY
            }

            override fun removeStack(slot: Int, amount: Int): ItemStack {
                return ItemStack.EMPTY
            }

            override fun setStack(slot: Int, stack: ItemStack) {
                if (stack.isEmpty) return

                entity.setStack(slot + 10, stack)
                onContentChanged(this)
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

    init {
        inventory.onOpen(player)

        //The Crafting Result
        this.addSlot(
            CraftingResultSlot(
                player,
                this.input,
                this.result,
                0,
                -999,
                -999
            )
        )
        //The Crafting Grid
        for (row in 0..2) {
            for (col in 0..2) {
                this.addSlot(
                    Slot(
                        this.input,
                        col + row * 3,
                        -999,
                        -999
                    )
                )
            }
        }
        //Our inventory
        for (row in 0..<INVENTORY_ROWS) {
            for (col in 0..<INVENTORY_COLS) {
                this.addSlot(
                    CraftingTableIISlot(
                        inventory,
                        col + row * INVENTORY_COLS,
                        8 + col * 18,
                        18 + row * 18
                    )
                )
            }
        }
        //The player inventory
        for (row in 0..2) {
            for (col in 0..8) {
                this.addSlot(
                    Slot(
                        player.inventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        125 + row * 18
                    )
                )
            }
        }
        //The player hotbar
        for (row in 0..8) {
            this.addSlot(
                Slot(
                    player.inventory,
                    row,
                    8 + row * 18,
                    184
                )
            )
        }

        if (player.world.isClient) {
            val playerInventory = player.inventory
            val recipeBook = (player as ClientPlayerEntity).recipeBook
            this.recipeHandler =
                RecipeManager(playerInventory, recipeBook, this)
            this.updateRecipes()
        }
    }

    override fun onSlotClick(
        slotIndex: Int,
        button: Int,
        actionType: SlotActionType,
        player: PlayerEntity
    ) {
        super.onSlotClick(slotIndex, button, actionType, player)
        if (player.world.isClient) {
            if (slotIndex == -999) return
            val slot = getSlot(slotIndex)

            if (slot.inventory == this.inventory) {
                val item = this.inventory.getStack(slotIndex - 10)
                val quickCraft = actionType == SlotActionType.QUICK_MOVE
                val recipe = this.recipeHandler?.getRecipe(item) ?: return
                val buf = PacketByteBufs.create() ?: return

                CraftingPacket(recipe.id, quickCraft).write(buf)

                ClientPlayNetworking.send(
                    ModMessages.CTII_CRAFT_RECIPE,
                    buf
                )
            }
        }
    }

    fun updateRecipes() {
        this.inventory.clear()
        this.recipeHandler?.getCraftableItemStacks()?.forEach { stack ->
            addRecipeItem(stack)
        }
    }

    override fun canInsertIntoSlot(index: Int): Boolean {
        return index != this.craftingResultSlotIndex
    }

    private fun addRecipeItem(stack: ItemStack) {
        for (i in 0 until this.inventory.size()) {
            if (this.inventory.getStack(i).isEmpty) {
                this.inventory.setStack(i, stack)
                return
            }
        }
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return true
    }

    override fun populateRecipeFinder(finder: RecipeMatcher?) {
        this.input.provideRecipeInputs(finder)
    }

    override fun clearCraftingSlots() {
        this.input.clear()
        this.result.clear()
    }

    fun updateResultSlot(itemStack: ItemStack) {
        this.result.setStack(0, itemStack)
    }

    override fun matches(recipe: Recipe<in RecipeInputInventory>): Boolean {
        return recipe.matches(this.input, this.player.world)
    }

    override fun getCraftingResultSlotIndex(): Int {
        return 0
    }

    override fun getCraftingWidth(): Int {
        return this.input.width
    }

    override fun getCraftingHeight(): Int {
        return this.input.height
    }

    override fun getCraftingSlotCount(): Int {
        return 10
    }

    override fun getCategory(): RecipeBookCategory {
        return RecipeBookCategory.CRAFTING
    }

    override fun quickMove(
        playerEntity: PlayerEntity,
        invSlot: Int
    ): ItemStack {
        return ItemStack.EMPTY
    }
}