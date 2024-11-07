package net.johnpgr.craftingtableiifabric.recipe

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection.RecipeFilterMode
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.recipebook.RecipeBookType
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.NetworkRecipeId
import net.minecraft.recipe.RecipeFinder
import net.minecraft.recipe.book.RecipeBookGroup
import net.minecraft.recipe.display.SlotDisplayContexts
import kotlin.jvm.optionals.getOrDefault

@Environment(EnvType.CLIENT)
class CraftingTableIIRecipeManager(
    private val screenHandler: CraftingTableIIScreenHandler,
    private val player: ClientPlayerEntity,
) {
    private val recipeFinder = RecipeFinder()
    private val recipeBook = player.recipeBook
    private val ctx = SlotDisplayContexts.createParameters(player.world)
    var results: List<Result> = listOf()

    /**
     * Refreshes the inputs for the recipe matcher and updates the list of recipes.
     * This method clears the current recipe matcher, populates it with the player's inventory
     * and the crafting screen handler, and retrieves the results for the crafting search group
     * from the player's recipe book.
     */
    fun refreshInputs() {
        recipeFinder.clear()
        player.inventory.populateRecipeFinder(recipeFinder)
        screenHandler.populateRecipeFinder(recipeFinder)
        refreshResults()
    }

    /**
     * Refreshes the list of craftable items based on the current state of the player's inventory and recipe book.
     * This method updates the `recipeItemStacks` property with the new list of craftable item stacks.
     */
    private fun refreshResults() {
        val recipes = recipeBook.getResultsForCategory(RecipeBookType.CRAFTING as RecipeBookGroup)
            .map { it.filter(RecipeFilterMode.CRAFTABLE) }

        this.results = recipes.stream().flatMap { entries ->
            entries.stream().map { entry ->
                Result(entry.id(), entry.getStacks(ctx), entry.craftingRequirements.getOrDefault(listOf()));
            }
        }.toList();
    }

    data class Result(val id: NetworkRecipeId, val displayItems: List<ItemStack>, val ingredients: List<Ingredient>) {
        fun getDisplayStack(currentIndex: Int): ItemStack {
            if (displayItems.isEmpty()) {
                return ItemStack.EMPTY
            } else {
                val i = currentIndex % displayItems.size
                return displayItems[i]
            }
        }

        fun getDisplayStack(): ItemStack {
            return getDisplayStack(0)
        }
    }
}