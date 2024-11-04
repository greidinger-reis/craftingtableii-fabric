package net.johnpgr.craftingtableiifabric.description

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.minecraft.client.MinecraftClient
import java.io.File
import kotlin.jvm.optionals.getOrNull

object CraftingTableIIDescriptions {
    private const val FALLBACK_LANG = "en_us"
    lateinit var descriptionsDict: HashMap<String, String>
    private var loaded = false

    fun descriptionFile(lang: String): File =
        File("${FabricLoader.getInstance().configDir}${File.separator}${CraftingTableIIMod.MOD_ID}${File.separator}descriptions${File.separator}${lang}.json")

    fun register() {
        ClientLifecycleEvents.CLIENT_STARTED.register({ client ->
            if (!loaded) {
                load(client)
                loaded = true
            }
        })
    }

    fun load(client: MinecraftClient) {
        CraftingTableIIMod.LOGGER.info("[${CraftingTableIIMod.MOD_ID}] Trying to read descriptions file...")
        val resourceManager = client.resourceManager
        var currentLang = client.languageManager.language
        val descriptionResource = resourceManager.getResource(
            CraftingTableIIMod.id("descriptions/${currentLang}.json")
        ).getOrNull() ?: run {
            currentLang = FALLBACK_LANG
            resourceManager.getResource(
                CraftingTableIIMod.id("descriptions/$FALLBACK_LANG.json")
            ).getOrNull() ?: run {
                CraftingTableIIMod.LOGGER.error("[${CraftingTableIIMod.MOD_ID}] Failed to load descriptions")
                return
            }
        }
        descriptionsDict = descriptionResource.toDescriptionsDict()

        try {
            val descriptionsFile = descriptionFile(currentLang)
            descriptionsFile.parentFile.mkdirs()
            if (descriptionsFile.createNewFile()) {
                CraftingTableIIMod.LOGGER.info("[${CraftingTableIIMod.MOD_ID}] No descriptions file found, creating a new one...")
                descriptionsDict.writeToFile(descriptionsFile)
                CraftingTableIIMod.LOGGER.info("[${CraftingTableIIMod.MOD_ID}] Successfully created default descriptions file.")
            } else {
                CraftingTableIIMod.LOGGER.info("[${CraftingTableIIMod.MOD_ID}] A descriptions file was found, loading it..")
                descriptionsDict = descriptionsFile.readAsDescriptionsDict()
                CraftingTableIIMod.LOGGER.info("[${CraftingTableIIMod.MOD_ID}] Successfully loaded descriptions file.")
            }
        } catch (ex: Exception) {
            CraftingTableIIMod.LOGGER.error(
                "[${CraftingTableIIMod.MOD_ID}] There was an error creating/loading the descriptions file!",
                ex
            )
            return
        }
    }
}
