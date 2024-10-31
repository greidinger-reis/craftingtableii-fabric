package net.johnpgr.craftingtableiifabric

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIEntityModel
import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIItemDynamicRenderer
import net.johnpgr.craftingtableiifabric.network.ModMessages
import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIDescriptions
import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIScreenHandler

object CraftingTableIIFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModBlocks.initBlocksClient()
        ModMessages.initPacketsClient()
        CraftingTableIIEntityModel.initClient()
        CraftingTableIIItemDynamicRenderer.initClient()
        CraftingTableIIDescriptions.initClient()
        ClientTickEvents.END_CLIENT_TICK.register(CraftingTableIIFabric.id("recipes_tick")) {
                client ->
            if(client.player?.currentScreenHandler is CraftingTableIIScreenHandler) {
                (client.player!!.currentScreenHandler as CraftingTableIIScreenHandler).tick()
            }
        }
    }
}
