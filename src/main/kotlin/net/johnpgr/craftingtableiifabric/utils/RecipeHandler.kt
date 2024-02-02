package net.johnpgr.craftingtableiifabric.utils

import com.google.common.collect.Lists
import net.minecraft.client.recipebook.ClientRecipeBook
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.registry.DynamicRegistryManager

class RecipeHandler(
    private val playerInventory: PlayerInventory,
    private val playerRecipeBook: ClientRecipeBook,
    private val registryManager: DynamicRegistryManager,
) {
    private val recipeMatcher = RecipeMatcher()
    private var cachedInvChangeCount = playerInventory.changeCount

    fun getRecipe(item: ItemStack): Recipe<*>? {
        val recipeList =
            playerRecipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
                .flatMap { it.getResults(false) }
        return recipeList.firstOrNull { it.getOutput(registryManager).item == item.item }
    }

    fun getOutputResults(): List<ItemStack> {
        refreshInputs()
        val recipes = getCraftableRecipes()
        return recipes.map { it.getOutput(registryManager) }
    }

    private fun getCraftableRecipes(): List<Recipe<*>> {
        val list =
            playerRecipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
        val list2 = Lists.newArrayList(list)

        list2.forEach {
            it.computeCraftables(
                recipeMatcher,
                9,
                9,
                playerRecipeBook
            )
        }
        list2.removeIf { !it.isInitialized || !it.hasFittingRecipes() || !it.hasCraftableRecipes() }

        val recipes = list2.flatMap {
            it.getResults(true)
        }
        return recipes
//        print("results: ")
//        results.forEach {
//            val output = it.getOutput(registryManager)
//            print("${output.count} ${it.id.path} ") }
//        print("\n")
    }

    private fun refreshInputs() {
        recipeMatcher.clear()
        playerInventory.populateRecipeFinder(recipeMatcher)
    }

    //TODO: This should listen to inventory updates
    fun update() {
        throw NotImplementedError()
    }

    init {
        refreshInputs()
    }
}