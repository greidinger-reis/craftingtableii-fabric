package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import com.google.common.collect.Lists
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList

class CraftingTableIIHandler(
    private val player: ClientPlayerEntity,
    private val registryManager: DynamicRegistryManager,
) {
    private val recipeMatcher = RecipeMatcher()
    private var cachedInvChangeCount = player.inventory.changeCount
    private var results = listOf<Recipe<*>>()

    fun getOutputResults(): List<ItemStack> {
        return results.map { it.getOutput(registryManager) }
    }

    fun getIngredients(identifier: Identifier): DefaultedList<Ingredient> {
        return results.first { it.id == identifier }.ingredients
            ?: DefaultedList.of()
    }

    fun craft(identifier: Identifier) {

    }

    private fun refreshResults() {
        val list =
            player.recipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
        val list2 = Lists.newArrayList(list)

        list2.forEach {
            it.computeCraftables(
                recipeMatcher,
                9,
                9,
                player.recipeBook
            )
        }
        list2.removeIf { !it.isInitialized || !it.hasFittingRecipes() || !it.hasCraftableRecipes() }

        results = list2.flatMap { result ->
            result.getResults(true)
        }

        print("Results: ")
        results.forEach { print( "${it.id} ") }
        print("\n")
    }

    private fun refreshInputs() {
        recipeMatcher.clear()
        player.inventory.populateRecipeFinder(recipeMatcher)
        //maybe this is needed in the CraftingTableIIScreenHandler? let's see
        //craftingScreenHandler.populateRecipeFinder(recipeFinder)
        refreshResults()
    }

    //TODO: This should listen to inventory updates
    private fun update() {
        if (player.inventory.changeCount != cachedInvChangeCount) {
            println("updating inputs. Prev inventory.changeCount: $cachedInvChangeCount")
            refreshInputs()
            cachedInvChangeCount = player.inventory.changeCount
        }
    }

    init {
        refreshInputs()
    }
}