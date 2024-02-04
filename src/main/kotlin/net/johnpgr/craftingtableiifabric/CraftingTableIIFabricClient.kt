package net.johnpgr.craftingtableiifabric

import net.fabricmc.api.ClientModInitializer
import net.johnpgr.craftingtableiifabric.network.ModMessages
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks

object CraftingTableIIFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModBlocks.initBlocksClient()
        ModMessages.initPacketsClient()
    }
}
