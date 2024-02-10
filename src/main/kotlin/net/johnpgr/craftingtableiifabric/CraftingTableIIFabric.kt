package net.johnpgr.craftingtableiifabric

import net.fabricmc.api.ModInitializer
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.johnpgr.craftingtableiifabric.network.ModMessages
import net.johnpgr.craftingtableiifabric.utils.CreativeTab
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object CraftingTableIIFabric : ModInitializer {
    const val MOD_ID = "craftingtableiifabric"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    fun id(name: String) = Identifier(MOD_ID, name)

    override fun onInitialize() {
        ModMessages.initPackets()
        ModBlocks.initBlocks()
        CreativeTab.initCreativeTab()
    }
}

