package net.johnpgr.craftingtableiifabric

import net.fabricmc.api.ModInitializer
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object CraftingTableIIFabric : ModInitializer {
    private val logger = LoggerFactory.getLogger("craftingtableiifabric")
    private const val MOD_ID = "craftingtableiifabric"

    fun id(name: String) = Identifier(MOD_ID, name)

    override fun onInitialize() {
        ModBlocks.registerBlockItemGroups()
    }
}