package net.johnpgr.craftingtableiifabric.recipe

import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeEntry
import net.minecraft.recipe.RecipeMatcher

class CraftingTableIIRecipeManager(
    private val screenHandler: CraftingTableIIScreenHandler,
    private val player: ClientPlayerEntity,
) {
    private val recipeMatcher = RecipeMatcher()
    private val recipeBook = player.recipeBook
    var results: List<RecipeResultCollection> = listOf()

    /**
     * Refreshes the inputs for the recipe matcher and updates the list of recipes.
     * This method clears the current recipe matcher, populates it with the player's inventory
     * and the crafting screen handler, and retrieves the results for the crafting search group
     * from the player's recipe book.
     */
    fun refreshInputs() {
        recipeMatcher.clear()
        player.inventory.populateRecipeFinder(recipeMatcher)
        screenHandler.populateRecipeFinder(recipeMatcher)
        refreshResults()
    }

    /**
     * Refreshes the list of craftable items based on the current state of the player's inventory and recipe book.
     * This method updates the `recipeItemStacks` property with the new list of craftable item stacks.
     */
    private fun refreshResults() {
        results = recipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
            .filter { resultCollection ->
                resultCollection.computeCraftables(
                    recipeMatcher,
                    screenHandler.craftingWidth,
                    screenHandler.craftingHeight,
                    recipeBook
                )
                resultCollection.isInitialized && resultCollection.hasFittingRecipes() && resultCollection.hasCraftableRecipes()
            }

        CraftingTableIIMod.LOGGER.info("Results count: ${results.size}")
    }

    companion object Extensions {
        fun RecipeResultCollection.firstItemStack(): Pair<ItemStack, RecipeEntry<*>> {
            val recipeEntry = getResults(true).first()
            val itemStack = recipeEntry.value.getResult(registryManager)

            return Pair(itemStack, recipeEntry)
        }

        //TODO: Figure a way to use this
        fun RecipeResultCollection.allItemStacks(): List<ItemStack> {
            return getResults(true).map { it.value.getResult(registryManager) }
        }
    }
}