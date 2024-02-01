package net.johnpgr.craftingtableiifabric.utils

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader

object FabricLoader {
    fun isClient(): Boolean {
        return FabricLoader.getInstance().environmentType == EnvType.CLIENT
    }
}