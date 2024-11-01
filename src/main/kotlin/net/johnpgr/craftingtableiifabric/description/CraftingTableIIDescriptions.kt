package net.johnpgr.craftingtableiifabric.description

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.johnpgr.craftingtableiifabric.utils.fromJson
import net.minecraft.client.MinecraftClient
import kotlin.jvm.optionals.getOrNull

object CraftingTableIIDescriptions {
    private const val FALLBACK_LANG = "en_us"
    var map: HashMap<String, String> = hashMapOf()

    fun register() {
        var run = false

        ClientTickEvents.START_WORLD_TICK.register(ClientTickEvents.StartWorldTick { world ->
            if (!run) {
                if (world != null) {
                    run = true
                    val resourceManager = MinecraftClient.getInstance().resourceManager
                    val currentLang = MinecraftClient.getInstance().languageManager.language
                    val description = resourceManager.getResource(
                        CraftingTableIIFabric.id("descriptions/${currentLang}.json")
                    )
                        .getOrNull() ?: resourceManager.getResource(
                        CraftingTableIIFabric.id("descriptions/$FALLBACK_LANG.json")
                    )
                        .getOrNull() ?: run {
                        CraftingTableIIFabric.LOGGER.error("Failed to load descriptions")
                        return@StartWorldTick
                    }

                    map = description.fromJson<HashMap<String, String>>() ?: run {
                        CraftingTableIIFabric.LOGGER.error("Failed to parse descriptions")
                        return@StartWorldTick
                    }
                }
            }
        })
    }
}

