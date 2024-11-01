package net.johnpgr.craftingtableiifabric.recipe

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeEntry
import net.minecraft.recipe.RecipeMatcher

class CraftingTableIIRecipeManager(
    private val craftingScreenHandler: CraftingTableIIScreenHandler,
    private val player: ClientPlayerEntity,
) {
    companion object {
        val ID = CraftingTableIIMod.id("recipe_manager_tick")

        fun register() {
            ClientTickEvents.END_CLIENT_TICK.register(ID) { client ->
                (client.player?.currentScreenHandler as? CraftingTableIIScreenHandler)?.tick()
            }
        }
    }

    private val recipeMatcher: RecipeMatcher = RecipeMatcher()
    private var recipes: List<RecipeResultCollection> = listOf()
    var recipeItemStacks: List<ItemStack> = listOf()

    /**
     * Refreshes the inputs for the recipe matcher and updates the list of recipes.
     * This method clears the current recipe matcher, populates it with the player's inventory
     * and the crafting screen handler, and retrieves the results for the crafting search group
     * from the player's recipe book.
     */
    private fun refreshInputs() {
        this.recipeMatcher.clear()
        this.player.inventory.populateRecipeFinder(this.recipeMatcher)
        this.craftingScreenHandler.populateRecipeFinder(this.recipeMatcher)
        this.recipes =
            this.player.recipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
    }

    /**
     * Scrolls through the list of craftable recipes based on the given scroll position.
     * Updates the current list index in the crafting screen handler and refreshes the displayed recipes.
     */
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
                        recipe.value.getResult(result.registryManager).copy()
                    })
            }
        }

        recipeItemStacks = newRecipeItemStacks
    }


    /**
     * Retrieves the recipe entry for the given item stack.
     * This method refreshes the inputs, searches through the list of recipes,
     * and returns the first recipe entry that matches the item in the stack.
     */
    fun getRecipe(stack: ItemStack): RecipeEntry<*> {
        this.refreshInputs()
        val recipeList =
            recipes
                .mapNotNull { result ->
                    result.getResults(false).firstOrNull { recipe ->
                        recipe.value.getResult(result.registryManager).item == stack.item
                    }
                }

        return recipeList.first()
    }
}