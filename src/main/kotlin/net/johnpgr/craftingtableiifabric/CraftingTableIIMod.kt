package net.johnpgr.craftingtableiifabric

import net.fabricmc.api.ModInitializer
import net.johnpgr.craftingtableiifabric.block.CraftingTableIIBlock
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity
import net.johnpgr.craftingtableiifabric.network.CraftingTableIIPacket
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler
import net.johnpgr.craftingtableiifabric.util.BlockEntityFactory
import net.johnpgr.craftingtableiifabric.util.BlockScreenHandlerFactory
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object CraftingTableIIMod : ModInitializer {
    const val MOD_ID = "craftingtableiifabric"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
    val BLOCK = CraftingTableIIBlock()
    val SCREEN_HANDLER = BlockScreenHandlerFactory.createHandlerType(::CraftingTableIIScreenHandler)
    val ENTITY_TYPE = BlockEntityFactory.createEntityType<CraftingTableIIEntity>(BLOCK)

    fun id(name: String) = Identifier(MOD_ID, name)

    override fun onInitialize() {
        CraftingTableIIBlock.register()
        CraftingTableIIEntity.register()
        CraftingTableIIScreenHandler.register()
        CraftingTableIIPacket.register()
    }
}

