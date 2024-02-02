package net.johnpgr.craftingtableiifabric.utils

import com.google.common.collect.Lists
import net.minecraft.client.recipebook.ClientRecipeBook
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeMatcher

class RecipeHandler(
    private val playerInventory: PlayerInventory,
    private val playerRecipeBook: ClientRecipeBook,
) {
    private val recipeMatcher = RecipeMatcher()
//    private var cachedInvChangeCount = playerInventory.changeCount

    fun getRecipe(stack: ItemStack): Recipe<*>? {
        val recipeList =
            playerRecipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
                .mapNotNull { result ->
                    result.getResults(false).firstOrNull { recipe ->
                        recipe.getOutput(result.registryManager).item == stack.item
                    }
                }

        if (recipeList.isEmpty()) {
            return null
        }

        return recipeList.first()
    }

    fun getCraftableItemStacks(): List<ItemStack> {
        refreshInputs()

        val list =
            playerRecipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
        list.forEach {
            it.computeCraftables(
                recipeMatcher,
                9,
                9,
                playerRecipeBook
            )
        }

        val list2 = Lists.newArrayList(list)

        list2.removeIf { !it.isInitialized || !it.hasFittingRecipes() || !it.hasCraftableRecipes() }

        return list2.flatMap { result ->
            result.getRecipes(true).map { recipe ->
                recipe.getOutput(result.registryManager).copy()
            }
        }
    }

    private fun refreshInputs() {
        recipeMatcher.clear()
        playerInventory.populateRecipeFinder(recipeMatcher)
    }
}