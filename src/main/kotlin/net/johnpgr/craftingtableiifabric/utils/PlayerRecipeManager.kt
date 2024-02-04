package net.johnpgr.craftingtableiifabric.utils

import com.google.common.collect.Lists
import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIScreenHandler
import net.minecraft.client.recipebook.ClientRecipeBook
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeMatcher

class PlayerRecipeManager(
    private val playerInventory: PlayerInventory,
    private val playerRecipeBook: ClientRecipeBook,
    private val craftingScreenHandler: CraftingTableIIScreenHandler
) {
    private val recipeMatcher = RecipeMatcher()

    fun getRecipe(stack: ItemStack): Recipe<*>? {
        val recipeList =
            this.playerRecipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
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
        this.refreshInputs()

        val recipeResultListRaw =
            playerRecipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
        recipeResultListRaw.forEach { result ->
            result.computeCraftables(
                this.recipeMatcher,
                9,
                9,
                this.playerRecipeBook
            )
        }

        val recipeResultList = Lists.newArrayList(recipeResultListRaw)

        recipeResultList.removeIf { result ->
            !result.isInitialized || !result.hasFittingRecipes() || !result.hasCraftableRecipes()
        }

        return recipeResultList.flatMap { result ->
            result.getRecipes(true).map { recipe ->
                recipe.getOutput(result.registryManager).copy()
            }
        }
    }

    private fun refreshInputs() {
        this.recipeMatcher.clear()
        this.playerInventory.populateRecipeFinder(this.recipeMatcher)
        this.craftingScreenHandler.populateRecipeFinder(this.recipeMatcher)
    }
}