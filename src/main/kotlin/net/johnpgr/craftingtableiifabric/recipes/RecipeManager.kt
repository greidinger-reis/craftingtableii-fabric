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
    private lateinit var recipes: List<RecipeResultCollection>
    lateinit var recipeItemStacks: List<ItemStack>

    private fun refreshInputs() {
        this.recipeMatcher.clear()
        this.player.inventory.populateRecipeFinder(this.recipeMatcher)
        this.craftingScreenHandler.populateRecipeFinder(this.recipeMatcher)
        this.recipes =
            this.player.recipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
    }

    fun scrollCraftableRecipes(scrollPos: Float) {
        val craftableRecipesSize = this.recipeItemStacks.size
        val i = (craftableRecipesSize + 8 - 1) / 8 - 5
        var j = ((scrollPos * i.toFloat()).toDouble() + 0.5).toInt()
        if (j < 0) {
            j = 0
        }

        val listIndex = j * 8
        if (listIndex < craftableRecipesSize) {
            this.craftingScreenHandler.currentListIndex =
                listIndex
        }

        this.craftingScreenHandler.updateRecipes()
    }

    fun refreshCraftableItems() {
        this.refreshInputs()
        this.recipes.forEach { result ->
            result.computeCraftables(
                this.recipeMatcher,
                9,
                9,
                this.player.recipeBook
            )
        }

        this.recipeItemStacks = this.recipes.filter { result ->
            result.isInitialized && result.hasFittingRecipes() && result.hasCraftableRecipes()
        }.flatMap { result ->
            result.getRecipes(true).map { recipe ->
                recipe.getOutput(result.registryManager).copy()
            }
        }
    }

    fun getRecipe(stack: ItemStack): Recipe<*> {
        this.refreshInputs()
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