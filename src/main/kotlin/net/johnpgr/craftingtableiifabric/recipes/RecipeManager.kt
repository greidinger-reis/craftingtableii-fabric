package net.johnpgr.craftingtableiifabric.recipes

import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableII
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
    var refreshes = 0

    private fun refreshInputs() {
        this.recipeMatcher.clear()
        this.player.inventory.populateRecipeFinder(this.recipeMatcher)
        this.craftingScreenHandler.populateRecipeFinder(this.recipeMatcher)
        this.recipes = this.player.recipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
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
            this.craftingScreenHandler.currentListIndex = listIndex
        }

        this.craftingScreenHandler.updateRecipes(false)
    }

    //TODO: This is the performance bottleneck in this mod.
    // This is being triggered 36 times on item craft and screen open
    // 2 times on a item move
    // with many mods installed, this lags the game a whole lot
    fun refreshCraftableItems() {
        ++refreshes
        val start = System.nanoTime()
        this.refreshInputs()
        this.recipes.forEach { result -> result.computeCraftables(this.recipeMatcher, 9, 9, this.player.recipeBook) }

        this.recipeItemStacks = this.recipes.filter { result ->
            result.isInitialized && result.hasFittingRecipes() && result.hasCraftableRecipes()
        }.flatMap { result ->
            result.getRecipes(true).map { recipe -> recipe.getOutput(result.registryManager).copy() }
        }
        val end = System.nanoTime()
        CraftingTableIIFabric.LOGGER.info("Refreshed craftable items in ${(end - start) / 1_000}μs #${refreshes}")
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