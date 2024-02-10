package net.johnpgr.craftingtableiifabric

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIEntityModel
import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIItemDynamicRenderer
import net.johnpgr.craftingtableiifabric.network.ModMessages

object CraftingTableIIFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModBlocks.initBlocksClient()
        ModMessages.initPacketsClient()

        CraftingTableIIEntityModel.getEntries()
            .forEach { (entityLayer, textureModelData) ->
                EntityModelLayerRegistry.registerModelLayer(entityLayer) { textureModelData }
            }
        BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.CRAFTING_TABLE_II, CraftingTableIIItemDynamicRenderer())
    }
}
