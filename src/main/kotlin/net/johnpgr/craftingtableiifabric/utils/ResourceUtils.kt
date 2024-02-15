package net.johnpgr.craftingtableiifabric.utils

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.minecraft.resource.Resource

inline fun <reified T> Resource.fromJson(): T? {
    val inputStream = this.inputStream
    val json = inputStream.bufferedReader().use { it.readText() }

    try {
        val res = Gson().fromJson(
            json, T::class.java
        ) as T
        return res
    } catch (e: JsonSyntaxException) {
        CraftingTableIIFabric.LOGGER.error("Failed to parse JSON from resource: $this")
        return null
    }
}
