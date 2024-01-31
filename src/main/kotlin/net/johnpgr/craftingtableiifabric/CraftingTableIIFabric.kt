package net.johnpgr.craftingtableiifabric

import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.johnpgr.craftingtableiifabric.utils.CreativeTab
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object CraftingTableIIFabric : ModInitializer {
    const val MOD_ID = "craftingtableiifabric"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)
    val CLIENT: Boolean by lazy { FabricLoader.getInstance().environmentType == EnvType.CLIENT }

    fun id(name: String) = Identifier(MOD_ID, name)

    override fun onInitialize() {
        ModBlocks.initBlocks()
        CreativeTab.initCreativeTab()
    }
}