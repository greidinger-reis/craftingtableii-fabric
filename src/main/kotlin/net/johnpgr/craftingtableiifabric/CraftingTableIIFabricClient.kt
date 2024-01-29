package net.johnpgr.craftingtableiifabric

import net.fabricmc.api.ClientModInitializer
import net.johnpgr.craftingtableiifabric.screens.ModScreens

object CraftingTableIIFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModScreens.clientRegisterHandledScreens()
    }
}
