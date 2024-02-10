package net.johnpgr.craftingtableiifabric

import net.fabricmc.api.ClientModInitializer
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIEntityModel
import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIItemDynamicRenderer
import net.johnpgr.craftingtableiifabric.network.ModMessages

object CraftingTableIIFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModBlocks.initBlocksClient()
        ModMessages.initPacketsClient()
        CraftingTableIIEntityModel.initClient()
        CraftingTableIIItemDynamicRenderer.initClient()
    }
}
