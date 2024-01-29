package net.johnpgr.craftingtableiifabric.screens

import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.johnpgr.craftingtableiifabric.screens.handlers.CraftingTableIIScreenHandler
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object ModScreens {
    val CRAFTING_TABLE_II_SCREEN_HANDLER = registerScreenHandler(
        CraftingTableIIFabric.id("crafting_table_ii_screen_handler"),
        ::CraftingTableIIScreenHandler
    )

    private fun <T : ScreenHandler> registerScreenHandler(
        id: Identifier,
        factory: ScreenHandlerType.Factory<T>
    ): ScreenHandlerType<T> {
        return Registry.register(
            Registries.SCREEN_HANDLER,
            CraftingTableIIFabric.id(
                "crafting_table_ii_screen_handler"
            ),
            ScreenHandlerType(factory, FeatureSet.empty())
        )
    }

    fun clientRegisterHandledScreens() {
        HandledScreens.register(
            CRAFTING_TABLE_II_SCREEN_HANDLER,
            ::CraftingTableIIScreen
        )
    }
}