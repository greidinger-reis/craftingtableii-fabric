package net.johnpgr.craftingtableiifabric.screen

import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.block.CraftingTableIIBlock
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIIInventory
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIISlot
import net.johnpgr.craftingtableiifabric.network.CraftingTableIIPacket
import net.johnpgr.craftingtableiifabric.recipe.CraftingTableIIRecipeManager
import net.johnpgr.craftingtableiifabric.recipe.CraftingTableIIRecipeManager.Extensions.first
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.screen.AbstractRecipeScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

//FIXME: Scrolling mouse on a resultInventory's slot when mouse wheel tweaks from mods are enabled cause ConcurrentModificationException
//FIXME: Inventory Profiles Next functions to this screen's inventory cause ConcurrentModificationException also
class CraftingTableIIScreenHandler(
    syncId: Int,
    val player: PlayerEntity,
    val entity: CraftingTableIIEntity,
    val blockContext: ScreenHandlerContext,
) : AbstractRecipeScreenHandler<RecipeInputInventory>(
    CraftingTableIIMod.SCREEN_HANDLER,
    syncId,
) {
    companion object {
        fun register() {
            Registry.register(
                Registries.SCREEN_HANDLER, CraftingTableIIBlock.ID, CraftingTableIIMod.SCREEN_HANDLER
            )
        }

        const val RESULT_INDEX = 0
        const val INPUT_INDEX_START = 1
        const val INPUT_INDEX_END = 9
        const val PLAYER_INVENTORY_INDEX_START = 9
        const val PLAYER_INVENTORY_INDEX_END = 36
        const val PLAYER_HOTBAR_INDEX_START = 36
        const val PLAYER_HOTBAR_INDEX_END = 45
        const val CTII_INVENTORY_INDEX_START = 45
        const val CTII_INVENTORY_INDEX_END = 85
    }

    var currentListIndex = 0
    val input = CraftingInventory(this, 3, 3)
    val result = CraftingResultInventory()
    val inventory = CraftingTableIIInventory(entity)
    lateinit var recipeManager: CraftingTableIIRecipeManager
    private var lastCraftedItem = ItemStack.EMPTY
    private var cachedInvChangeCount = -1

    init {
        //The Crafting Result
        addSlot(
            CraftingResultSlot(
                player, input, result, 0, -999, -999
            )
        )

        //The Crafting Grid
        for (row in 0 until 3) {
            for (col in 0 until 3) {
                addSlot(Slot(input, col + row * 3, -999, -999))
            }
        }

        //The player inventory
        for (row in 0 until 3) {
            for (col in 0 until 9) {
                addSlot(
                    Slot(
                        player.inventory, col + row * 9 + 9, 8 + col * 18, 125 + row * 18
                    )
                )
            }
        }

        //The player hotbar
        for (row in 0 until 9) {
            addSlot(Slot(player.inventory, row, 8 + row * 18, 184))
        }

        //Our inventory
        for (row in 0 until CraftingTableIIInventory.ROWS) {
            for (col in 0 until CraftingTableIIInventory.COLS) {
                addSlot(
                    CraftingTableIISlot(
                        inventory, col + row * CraftingTableIIInventory.COLS, 8 + col * 18, 18 + row * 18
                    )
                )
            }
        }

        if (player.world.isClient) {
            recipeManager = CraftingTableIIRecipeManager(this, player as ClientPlayerEntity)
        }
    }

    fun tick() {
        if (cachedInvChangeCount != player.inventory.changeCount) {
            cachedInvChangeCount = player.inventory.changeCount
            updateRecipes(true)
        }
    }

    private fun addRecipeItem(stack: ItemStack, recipe: Recipe<*>) {
        for (i in 0 until inventory.size()) {
            if (inventory.getStack(i).isEmpty) {
                inventory.setStack(i, stack, recipe)
                return
            }
        }
    }

    private fun isValidCTIISlot(index: Int): Boolean {
        return index >= 0 && index < slots.size
    }

    override fun onSlotClick(
        slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity
    ) {
        super.onSlotClick(slotIndex, button, actionType, player)

        if (!player.world.isClient || !isValidCTIISlot(slotIndex) || button != 0) return

        val slot = getSlot(slotIndex) as? CraftingTableIISlot ?: return
        if (!slot.hasStack()) return

        val quickCraft = actionType == SlotActionType.QUICK_MOVE
        val itemStack = slot.stack
        val recipe = slot.recipe ?: return

        CraftingTableIIPacket(recipe.id, syncId, quickCraft).send()

        lastCraftedItem = itemStack
    }

    private fun validateLastCrafted(results: List<RecipeResultCollection>): Pair<ItemStack, Recipe<*>>? {
        results.forEach { result ->
            val pair = result.first()
            if (ItemStack.areItemsEqual(pair.first, lastCraftedItem)) {
                return pair
            }
        }
        return null
    }

    fun updateRecipes(shouldRefreshInputs: Boolean) {
        inventory.clear()
        if (shouldRefreshInputs) recipeManager.refreshInputs()

        val newList = mutableListOf<Pair<ItemStack, Recipe<*>>>()

        validateLastCrafted(recipeManager.results)?.let {
            newList.add(it)
        } ?: run {
            lastCraftedItem = ItemStack.EMPTY
        }

        val max = CraftingTableIIInventory.SIZE - newList.size

        for (i in currentListIndex until currentListIndex + max) {
            recipeManager.results.getOrNull(i)?.let {
                newList.add(it.first())
            } ?: break
        }

        newList.forEach { addRecipeItem(it.first, it.second) }
    }

    override fun canInsertIntoSlot(index: Int): Boolean {
        return index != craftingResultSlotIndex
    }

    /**
     * Checks if the player can use the crafting table.
     * This method verifies if the block at the given position is the crafting table block
     * and if the player is within a 64-block radius of the block.
     */
    override fun canUse(player: PlayerEntity): Boolean {
        return blockContext.get({ world: World, blockPos: BlockPos ->
            if (world.getBlockState(
                    blockPos
                ).block != CraftingTableIIMod.BLOCK
            ) false else player.squaredDistanceTo(
                blockPos.x + .5, blockPos.y + .5, blockPos.z + .5
            ) < 64.0
        }, true)
    }

    override fun populateRecipeFinder(finder: RecipeMatcher?) {
        input.provideRecipeInputs(finder)
    }

    override fun clearCraftingSlots() {
        input.clear()
        result.clear()
    }

    override fun matches(recipe: Recipe<in RecipeInputInventory>): Boolean {
        return recipe.matches(input, player.world)
    }

    fun updateResultSlot(itemStack: ItemStack) {
        result.setStack(0, itemStack)
    }

    override fun getCraftingResultSlotIndex(): Int {
        return 0
    }

    override fun getCraftingWidth(): Int {
        return input.width
    }

    override fun getCraftingHeight(): Int {
        return input.height
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
}