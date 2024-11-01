package net.johnpgr.craftingtableiifabric

import net.fabricmc.api.ClientModInitializer
import net.johnpgr.craftingtableiifabric.description.CraftingTableIIDescriptions
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityModel
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityRenderer
import net.johnpgr.craftingtableiifabric.recipe.CraftingTableIIRecipeManager
import net.johnpgr.craftingtableiifabric.renderer.CraftingTableIIItemDynamicRenderer
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreen

object CraftingTableIIFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        CraftingTableIIScreen.register()
        CraftingTableIIEntityModel.register()
        CraftingTableIIEntityRenderer.register()
        CraftingTableIIItemDynamicRenderer.register()
        CraftingTableIIDescriptions.register()
        CraftingTableIIRecipeManager.register()
    }
}
