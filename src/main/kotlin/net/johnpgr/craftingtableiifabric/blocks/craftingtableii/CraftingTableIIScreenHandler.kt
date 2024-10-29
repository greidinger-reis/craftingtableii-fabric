package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.johnpgr.craftingtableiifabric.network.ModMessages
import net.johnpgr.craftingtableiifabric.network.packet.CraftingPacket
import net.johnpgr.craftingtableiifabric.recipes.RecipeManager
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.screen.AbstractRecipeScreenHandler
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import java.util.Collections

class CraftingTableIIScreenHandler(
    syncId: Int,
    val player: PlayerEntity,
    val entity: CraftingTableIIEntity,
) : AbstractRecipeScreenHandler<RecipeInputInventory>(
    ModBlocks.getContainerInfo(ModBlocks.CRAFTING_TABLE_II)?.handlerType,
    syncId,
) {
    val inventory = CraftingTableIIInventory(entity, this)
    val inputInventory = CraftingInventory(this, 3, 3)
    val resultInventory = CraftingResultInventory()
    lateinit var recipeManager: RecipeManager
    var currentListIndex = 0
    var lastCraftedItem = ItemStack.EMPTY

    init {
        inventory.onOpen(player)

        //The Crafting Result
        addSlot(CraftingResultSlot(player, inputInventory, resultInventory, 0, -999, -999))

        //The Crafting Grid
        for (row in 0..2) {
            for (col in 0..2) {
                addSlot(Slot(inputInventory, col + row * 3, -999, -999))
            }
        }

        //Our inventory
        for (row in 0..<CraftingTableIIInventory.ROWS) {
            for (col in 0..<CraftingTableIIInventory.COLS) {
                addSlot(
                    CraftingTableIISlot(
                        inventory,
                        col + row * CraftingTableIIInventory.COLS,
                        8 + col * 18,
                        18 + row * 18
                    )
                )
            }
        }

        //The player inventory
        for (row in 0..2) {
            for (col in 0..8) {
                addSlot(Slot(player.inventory, col + row * 9 + 9, 8 + col * 18, 125 + row * 18))
            }
        }

        //The player hotbar
        for (row in 0..8) {
            addSlot(Slot(player.inventory, row, 8 + row * 18, 184))
        }

        if (player.world.isClient) {
            recipeManager = RecipeManager(this, player as ClientPlayerEntity)
        }
    }

    private fun addRecipeItem(stack: ItemStack) {
        for (i in 0 until inventory.size()) {
            if (inventory.getStack(i).isEmpty) {
                inventory.setStack(i, stack)
                return
            }
        }
    }

    override fun isValid(slot: Int): Boolean {
        return slot >= 0 && slot < slots.size
    }

    override fun onSlotClick(
        slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity
    ) {
        super.onSlotClick(slotIndex, button, actionType, player)

        //Only allow left clicks
        if(button != 0) return

        if (player.world.isClient) {
            if (!isValid(slotIndex)) return
            val slot = getSlot(slotIndex)
            if (!slot.hasStack()) return

            if (slot is CraftingTableIISlot) {
                val quickCraft = actionType == SlotActionType.QUICK_MOVE
                val recipe = recipeManager.getRecipe(slot.stack)
                val buf = PacketByteBufs.create() ?: return

                CraftingPacket(recipe.id, syncId, quickCraft).write(buf)
                ClientPlayNetworking.send(ModMessages.CTII_CRAFT_RECIPE, buf)

                lastCraftedItem = slot.stack
            }
        }
    }

    fun updateRecipes() {
        //Because the recipeManager only lives in the client
        if (!player.world.isClient) return

        inventory.clear()
        recipeManager.refreshCraftableItems()

        var max = CraftingTableIIInventory.SIZE
        var isLastCraftedItemStillValid = false
        val newList: MutableList<Int> = mutableListOf()

        for (i in currentListIndex until currentListIndex + max) {
            val itemStack = recipeManager.recipeItemStacks.getOrNull(i) ?: break

            if(itemStack.item == lastCraftedItem.item) {
                isLastCraftedItemStillValid = true
                newList.add(i)
                newList.add(0, i)
                --max
                continue
            }
            newList.add(i)
        }

        newList.forEach { i ->
            addRecipeItem(recipeManager.recipeItemStacks[i])
        }

        if(!isLastCraftedItemStillValid) {
            lastCraftedItem = ItemStack.EMPTY
        }
    }

    override fun canInsertIntoSlot(index: Int): Boolean {
        return index != craftingResultSlotIndex
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return true
    }

    override fun populateRecipeFinder(finder: RecipeMatcher?) {
        inputInventory.provideRecipeInputs(finder)
    }

    override fun clearCraftingSlots() {
        inputInventory.clear()
        resultInventory.clear()
    }

    fun updateResultSlot(itemStack: ItemStack) {
        resultInventory.setStack(0, itemStack)
    }

    override fun matches(recipe: Recipe<in RecipeInputInventory>): Boolean {
        return recipe.matches(inputInventory, player.world)
    }

    override fun getCraftingResultSlotIndex(): Int {
        return 0
    }

    override fun getCraftingWidth(): Int {
        return inputInventory.width
    }

    override fun getCraftingHeight(): Int {
        return inputInventory.height
    }

    override fun getCraftingSlotCount(): Int {
        return 10
    }

    override fun getCategory(): RecipeBookCategory {
        return RecipeBookCategory.CRAFTING
    }

    override fun quickMove(
        playerEntity: PlayerEntity, invSlot: Int
    ): ItemStack {
        return ItemStack.EMPTY
    }

    class CraftingTableIISlot(
        inventory: Inventory, index: Int, x: Int, y: Int
    ) : Slot(
        inventory, index, x, y
    ) {
        override fun canInsert(stack: ItemStack): Boolean {
            return false
        }
    }
}