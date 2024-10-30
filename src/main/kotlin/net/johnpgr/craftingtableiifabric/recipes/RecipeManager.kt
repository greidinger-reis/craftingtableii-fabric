package net.johnpgr.craftingtableiifabric.recipes

import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIScreenHandler
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeMatcher

class RecipeManager(
    private val craftingScreenHandler: CraftingTableIIScreenHandler,
    private val player: ClientPlayerEntity,
) {
    private val recipeMatcher: RecipeMatcher = RecipeMatcher()
    private var recipes: List<RecipeResultCollection> = listOf()
    var recipeItemStacks: List<ItemStack> = listOf()

    private fun refreshInputs() {
        recipeMatcher.clear()
        player.inventory.populateRecipeFinder(recipeMatcher)
        craftingScreenHandler.populateRecipeFinder(recipeMatcher)
        recipes =
            player.recipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
    }

    fun scrollCraftableRecipes(scrollPos: Float) {
        val craftableRecipesSize = recipeItemStacks.size
        val i = (craftableRecipesSize + 8 - 1) / 8 - 5
        var j = ((scrollPos * i.toFloat()).toDouble() + 0.5).toInt()
        if (j < 0) {
            j = 0
        }

        val listIndex = j * 8
        if (listIndex < craftableRecipesSize) {
            craftingScreenHandler.currentListIndex = listIndex
        }

        craftingScreenHandler.updateRecipes(false)
    }


    /**
     * Refreshes the list of craftable items based on the current state of the player's inventory and recipe book.
     * This method updates the `recipeItemStacks` property with the new list of craftable item stacks.
     */
    fun refreshCraftableItems() {
        refreshInputs()
        val newRecipeItemStacks = mutableListOf<ItemStack>()

        recipes.forEach { result ->
            result.computeCraftables(recipeMatcher, 9, 9, player.recipeBook)
            if (result.isInitialized && result.hasFittingRecipes() && result.hasCraftableRecipes()) {
                newRecipeItemStacks.addAll(
                    result.getRecipes(true).map { recipe ->
                        recipe.getOutput(result.registryManager).copy()
                    })
            }
        }

        recipeItemStacks = newRecipeItemStacks
    }

    fun getRecipe(stack: ItemStack): Recipe<*> {
        refreshInputs()
        val recipeList =
            recipes
                .mapNotNull { result ->
                    result.getResults(false).firstOrNull { recipe ->
                        recipe.getOutput(result.registryManager).item == stack.item
                    }
                }

        return recipeList.first()
    }
}