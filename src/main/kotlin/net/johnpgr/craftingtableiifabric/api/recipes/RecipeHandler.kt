package net.johnpgr.craftingtableiifabric.api.recipes

import com.google.common.collect.Lists
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.recipebook.ClientRecipeBook
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList

@Environment(EnvType.CLIENT)
class RecipeHandler(
    private val playerInventory: PlayerInventory,
    private val playerRecipeBook: ClientRecipeBook,
    private val registryManager: DynamicRegistryManager,
) {
    private val recipeMatcher = RecipeMatcher()
    private var cachedInvChangeCount = playerInventory.changeCount
    private var results = listOf<Recipe<*>>()

    //TODO
    fun getDescription(stack: ItemStack): Text {
        throw NotImplementedError()
    }

    fun getOutputResults(): List<ItemStack> {
        return results.map { it.getOutput(registryManager) }
    }

    fun getIngredients(identifier: Identifier): DefaultedList<Ingredient> {
        return results.first { it.id == identifier }.ingredients
            ?: DefaultedList.of()
    }

    private fun refreshResults() {
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

        results = list2.flatMap { result ->
            result.getResults(true)
        }
    }

    private fun refreshInputs() {
        recipeMatcher.clear()
        playerInventory.populateRecipeFinder(recipeMatcher)
        refreshResults()
    }

    //TODO: This should listen to inventory updates
    fun update() {
        throw NotImplementedError()
    }

    init {
        refreshInputs()
    }
}