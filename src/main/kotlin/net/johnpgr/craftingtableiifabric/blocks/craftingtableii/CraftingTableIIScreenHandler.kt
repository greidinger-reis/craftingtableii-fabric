package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.johnpgr.craftingtableiifabric.network.ModMessages
import net.johnpgr.craftingtableiifabric.network.packet.CraftingPacket
import net.johnpgr.craftingtableiifabric.utils.PlayerRecipeManager
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.screen.AbstractRecipeScreenHandler
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

class CraftingTableIIScreenHandler(
    syncId: Int,
    val player: PlayerEntity,
    val entity: CraftingTableIIBlockEntity,
) : AbstractRecipeScreenHandler<RecipeInputInventory>(
    ModBlocks.getContainerInfo(ModBlocks.CRAFTING_TABLE_II)?.handlerType,
    syncId,
) {
    val inventory = CraftingTableIIInventory(entity, this)
    val inputInventory = CraftingInventory(this, 3, 3)
    val resultInventory = CraftingResultInventory()
    var playerRecipeManager: PlayerRecipeManager? = null

    init {
        inventory.onOpen(player)

        //The Crafting Result
        this.addSlot(
            CraftingResultSlot(
                player,
                this.inputInventory,
                this.resultInventory,
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
                        this.inputInventory,
                        col + row * 3,
                        -999,
                        -999
                    )
                )
            }
        }
        //Our inventory
        for (row in 0..<CraftingTableIIInventory.ROWS) {
            for (col in 0..<CraftingTableIIInventory.COLS) {
                this.addSlot(
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
            this.playerRecipeManager =
                PlayerRecipeManager(playerInventory, recipeBook, this)
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
                val recipe = this.playerRecipeManager?.getRecipe(item) ?: return
                val buf = PacketByteBufs.create() ?: return

                CraftingPacket(recipe.id, this.syncId, quickCraft).write(buf)

                ClientPlayNetworking.send(
                    ModMessages.CTII_CRAFT_RECIPE,
                    buf
                )
            }
        }
    }

    fun updateRecipes() {
        this.inventory.clear()
        this.playerRecipeManager?.getCraftableItemStacks()?.forEach { stack ->
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
        this.inputInventory.provideRecipeInputs(finder)
    }

    override fun clearCraftingSlots() {
        this.inputInventory.clear()
        this.resultInventory.clear()
    }

    fun updateResultSlot(itemStack: ItemStack) {
        this.resultInventory.setStack(0, itemStack)
    }

    override fun matches(recipe: Recipe<in RecipeInputInventory>): Boolean {
        return recipe.matches(this.inputInventory, this.player.world)
    }

    override fun getCraftingResultSlotIndex(): Int {
        return 0
    }

    override fun getCraftingWidth(): Int {
        return this.inputInventory.width
    }

    override fun getCraftingHeight(): Int {
        return this.inputInventory.height
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