package net.johnpgr.craftingtableiifabric.screen

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.block.CraftingTableIIBlock
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIIInventory
import net.johnpgr.craftingtableiifabric.network.CraftingTableIIPacket
import net.johnpgr.craftingtableiifabric.recipe.CraftingTableIIRecipeManager
import net.johnpgr.craftingtableiifabric.recipe.CraftingTableIIRecipeManager.Extensions.firstItemStack
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeEntry
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
//FIXME: When the player drags an item in their inventory, the cursor stack doesn't get updated.
// That duplicates the item stack in the inventory.
// If the player then clicks on a slot, the full amount of items that were supposed to be split are moved to the slot.
class CraftingTableIIScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    entity: CraftingTableIIEntity,
    private val context: ScreenHandlerContext,
) : AbstractRecipeScreenHandler<RecipeInputInventory>(
    CraftingTableIIMod.SCREEN_HANDLER,
    syncId,
) {
    companion object {
        fun register() {
            Registry.register(
                Registries.SCREEN_HANDLER,
                CraftingTableIIBlock.ID,
                CraftingTableIIMod.SCREEN_HANDLER
            )
        }
    }

    var currentListIndex = 0
    val input = CraftingInventory(this, 3, 3)
    val result = CraftingResultInventory()
    val inventory = CraftingTableIIInventory(entity)
    lateinit var recipeManager: CraftingTableIIRecipeManager
    private val player = playerInventory.player
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
                        playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        125 + row * 18
                    )
                )
            }
        }

        //The player hotbar
        for (row in 0 until 9) {
            addSlot(Slot(playerInventory, row, 8 + row * 18, 184))
        }

        //Our inventory
        for (row in 0 until CraftingTableIIInventory.ROWS) {
            for (col in 0 until CraftingTableIIInventory.COLS) {
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

        if (player.world.isClient) {
            recipeManager =
                CraftingTableIIRecipeManager(this, player as ClientPlayerEntity)
        }
    }

    fun tick() {
        if (cachedInvChangeCount != player.inventory.changeCount) {
            cachedInvChangeCount = player.inventory.changeCount
            updateRecipes(true)
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

    private fun getValidSlot(index: Int): Slot? {
        if (!(index >= 0 && index < slots.size)) return null
        return super.getSlot(index)
    }

    override fun onSlotClick(
        slotIndex: Int,
        button: Int,
        actionType: SlotActionType,
        player: PlayerEntity
    ) {
        CraftingTableIIMod.LOGGER.info("[${if (player.world.isClient) "CLIENT" else "SERVER"}]: slotIndex: $slotIndex, button: $button, actionType: $actionType")
        super.onSlotClick(slotIndex, button, actionType, player)

        val slot = getValidSlot(slotIndex) ?: return
        if (slot is CraftingTableIISlot && button == 0) {
            if (!slot.hasStack()) {
                return
            }

            if (!player.world.isClient) {
                return
            }

            val quickCraft = actionType == SlotActionType.QUICK_MOVE
            val recipe = recipeManager.getRecipe(slot.stack)
            val payload = CraftingTableIIPacket(
                recipe.id, syncId, quickCraft
            )
            ClientPlayNetworking.send(payload)
            lastCraftedItem = slot.stack.copy()
        }
    }

    fun updateRecipes(shouldRefreshInputs: Boolean) {
        CraftingTableIIMod.LOGGER.info("Updating recipes")
        inventory.clear()
        if (shouldRefreshInputs) recipeManager.refreshInputs()

        var max = CraftingTableIIInventory.SIZE
        var isLastCraftedItemStillValid = false
        val newList: MutableList<ItemStack> = mutableListOf()

        for (i in currentListIndex until currentListIndex + max) {
            val result = recipeManager.results.getOrNull(i) ?: break
            val itemStack = result.firstItemStack()

            if (itemStack.item == lastCraftedItem.item) {
                isLastCraftedItemStillValid = true
                newList.add(0, itemStack.copy())
                --max
            }
            newList.add(itemStack.copy())
        }

        newList.forEach { addRecipeItem(it) }

        if (!isLastCraftedItemStillValid) {
            lastCraftedItem = ItemStack.EMPTY
        }
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
        return context.get({ world: World, blockPos: BlockPos ->
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

    override fun matches(recipe: RecipeEntry<out Recipe<RecipeInputInventory>>): Boolean {
        return recipe.value.matches(input, player.world)
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