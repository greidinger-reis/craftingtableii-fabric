package net.johnpgr.craftingtableiifabric.recipe

import com.google.common.collect.Lists
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
     * Scrolls through the list of craftable recipes based on the given scroll position.
     * Updates the current list index in the crafting screen handler and refreshes the displayed recipes.
     */
    fun scroll(scrollPos: Float) {
        val size = this.results.size
        val i = (size + 8 - 1) / 8 - 5
        var j = ((scrollPos * i.toFloat()).toDouble() + 0.5).toInt()
        if (j < 0) {
            j = 0
        }

        val listIndex = j * 8
        if (listIndex < size) {
            this.screenHandler.currentListIndex = listIndex
        }

        this.screenHandler.updateRecipes(false)
    }

    /**
     * Refreshes the list of craftable items based on the current state of the player's inventory and recipe book.
     * This method updates the `recipeItemStacks` property with the new list of craftable item stacks.
     */
    private fun refreshResults() {
        val list: List<RecipeResultCollection> =
            recipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
        list.forEach({ resultCollection: RecipeResultCollection ->
            resultCollection.computeCraftables(
               recipeMatcher,
                screenHandler.getCraftingWidth(),
                screenHandler.getCraftingHeight(),
                recipeBook
            )
        })
        val list2 = Lists.newArrayList(list)
        list2.removeIf { resultCollection: RecipeResultCollection -> !resultCollection.isInitialized }
        list2.removeIf { resultCollection: RecipeResultCollection -> !resultCollection.hasFittingRecipes() }
        list2.removeIf { resultCollection: RecipeResultCollection -> !resultCollection.hasCraftableRecipes() }
        results = list2
    }

    /**
     * Retrieves the recipe entry for the given item stack.
     * This method refreshes the inputs, searches through the list of recipes,
     * and returns the first recipe entry that matches the item in the stack.
     */
    fun getRecipe(stack: ItemStack): RecipeEntry<*> {
        val recipeList =
            results
                .mapNotNull { result ->
                    result.getResults(false).firstOrNull { recipe ->
                        recipe.value.getResult(result.registryManager).item == stack.item
                    }
                }

        return recipeList.first()
    }

    companion object Extensions {
        /**
         * Retrieves the first item stack from the recipe result collection.
         */
        fun RecipeResultCollection.firstItemStack(): ItemStack {
            return getResults(true).first().value.getResult(registryManager)
        }
        /**
         * Retrieves all item stacks from the recipe result collection.
         */
        fun RecipeResultCollection.allItemStacks(): List<ItemStack> {
            return getResults(true).map { it.value.getResult(registryManager) }
        }
    }
}