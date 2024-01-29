package net.johnpgr.craftingtableiifabric.api

import com.google.common.collect.Sets
import net.minecraft.recipe.Recipe
import net.minecraft.util.Identifier

data class RecipeManager(
    val recipes: MutableSet<Identifier> = Sets.newHashSet(),
    val toBeDisplayed: MutableSet<Identifier> = Sets.newHashSet()
)

fun RecipeManager.contains(recipe: Recipe<*>): Boolean {
    return this.contains(recipe.id)
}

fun RecipeManager.contains(id: Identifier): Boolean {
    return this.recipes.contains(id)
}

fun RecipeManager.add(recipe: Recipe<*>) {
    this.add(recipe.id)
}

fun RecipeManager.add(id: Identifier) {
    this.recipes.add(id)
}

fun RecipeManager.remove(recipe: Recipe<*>) {
    this.remove(recipe.id)
}

fun RecipeManager.remove(id: Identifier) {
    this.recipes.remove(id)
}

fun RecipeManager.shouldDisplay(recipe: Recipe<*>): Boolean {
    return this.toBeDisplayed.contains(recipe.id)
}

fun RecipeManager.onRecipeDisplayed(recipe: Recipe<*>) {
    this.toBeDisplayed.remove(recipe.id)
}

fun RecipeManager.display(recipe: Recipe<*>) {
    this.display(recipe.id)
}

fun RecipeManager.display(id: Identifier) {
    this.toBeDisplayed.add(id)
}