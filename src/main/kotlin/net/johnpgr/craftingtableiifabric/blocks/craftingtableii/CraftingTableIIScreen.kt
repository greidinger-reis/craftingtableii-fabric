package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import com.google.gson.Gson
import com.mojang.blaze3d.systems.RenderSystem
import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper
import kotlin.jvm.optionals.getOrNull

class CraftingTableIIScreen(
    screenHandler: CraftingTableIIScreenHandler,
    player: PlayerEntity,
    title: Text
) : HandledScreen<CraftingTableIIScreenHandler>(
    screenHandler, player.inventory, title
) {
    companion object {
        private const val DESCRIPTIONS_BASE_PATH = "descriptions/lang"
    }

    private val currentLang: String
        get() = this.client?.languageManager?.language ?: "en_us"
    private val descriptionsIdentifier = CraftingTableIIFabric.id(
        "${DESCRIPTIONS_BASE_PATH}/${currentLang}.json"
    )
    private var descriptionsMap: HashMap<String, String>? = null
    private val texture = CraftingTableIIFabric.id(
        "textures/gui/crafttableii.png"
    )
    private val descriptionTexture = CraftingTableIIFabric.id(
        "textures/gui/crafttableii_description.png"
    )
    private var scrollPosition = 0.0f

    override fun init() {
        super.init()
        backgroundHeight = 208
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 11
        playerInventoryTitleY = backgroundHeight - 97
        x = width / 2 - backgroundWidth / 2
        y = height / 2 - backgroundHeight / 2
        this.loadDescriptions()
    }

    override fun render(
        context: DrawContext, mouseX: Int, mouseY: Int, delta: Float
    ) {
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    override fun mouseScrolled(
        mouseX: Double, mouseY: Double, amount: Double
    ): Boolean {
        val craftableRecipesSize =
            this.screenHandler.recipeManager?.recipeItemStacks?.size
                ?: return false
        if (craftableRecipesSize <= CraftingTableIIInventory.SIZE) {
            return false
        }

        val aX = mouseX - this.x
        val aY = mouseY - this.y

        //check if the mouse is in our inventory bounds
        if ((aX >= 0 && aY >= 0 && aX < 176) && aY < this.backgroundHeight - 100) {
            val i = ((craftableRecipesSize + 8 - 1) / 8 - 5).toDouble()
            val j = MathHelper.clamp(amount, -1.0, 1.0)

            this.scrollPosition -= (j / i).toFloat()
            this.scrollPosition =
                MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f)
            this.screenHandler.recipeManager?.scrollCraftableRecipes(
                scrollPosition
            )

            return true
        }

        return false
    }

    override fun drawBackground(
        ctx: DrawContext, delta: Float, mouseX: Int, mouseY: Int
    ) {
        renderBackground(ctx)
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)

        //draw inventory
        ctx.drawTexture(
            this.texture, x, y, 0, 0, backgroundWidth, backgroundHeight
        )

        val k1 = y + 17
        val l1 = k1 + 88 + 2
        val craftableRecipesSize =
            this.screenHandler.recipeManager?.recipeItemStacks?.size ?: 0

        //draw scrollbar
        ctx.drawTexture(
            this.texture,
            x + 154,
            y + 17 + ((l1 - k1 - 17).toFloat() * scrollPosition).toInt(),
            if (craftableRecipesSize <= CraftingTableIIInventory.SIZE) 16 else 0,
            208,
            16,
            16
        )

        for (i in 10 until 50) {
            val slot = this.screenHandler.getSlot(i)
            if (slot is CraftingTableIIScreenHandler.CraftingTableIISlot && isMouseOverSlot(
                    slot,
                    mouseX,
                    mouseY
                )
            ) {
                if (slot.stack.isEmpty) continue

                //draw description overlay
                ctx.drawTexture(
                    this.descriptionTexture,
                    x - 124,
                    y,
                    0,
                    0,
                    121,
                    162
                )

                val recipe =
                    this.screenHandler.recipeManager!!.getRecipe(slot.stack)

                val recipeStacks = arrayListOf<ItemStack>()

                //TODO: Find a way to draw all matching stacks. Maybe a timer that loops through the list of matching stacks
                for (ingredient in recipe.ingredients) {
                    if (ingredient.isEmpty) continue

                    val item = ingredient.matchingStacks[0]
                    val index =
                        recipeStacks.indexOfFirst { it.item == item.item }

                    if (index == -1) {
                        recipeStacks.add(item.copy())
                        continue
                    }
                    recipeStacks[index].count += item.count
                }

                recipeStacks.forEachIndexed { r, stack ->
                    ctx.drawItem(
                        stack,
                        x - 25,
                        y + 5 + r * 18
                    )
                    ctx.drawItemInSlot(
                        this.client!!.textRenderer,
                        stack,
                        x - 25,
                        y + 5 + r * 18
                    )
                }

                val output =
                    recipe.getOutput(this.client!!.world!!.registryManager)

                val titleX = x - 118
                val titleY = y + 9

                val title = if (output.name.string.length > 16) {
                    output.name.string.substring(0, 16) + "..."
                } else {
                    output.name.string
                }

                //draw title
                ctx.drawText(
                    this.client!!.textRenderer,
                    title,
                    titleX,
                    titleY,
                    0xFFFFFF,
                    false,
                )

                val description =
                    descriptionsMap?.get(output.translationKey) ?: ""
                val chunks = this.chunkDescription(description)
                val descY = titleY + 2
                val scalef = 0.5f

                ctx.matrices.push()
                ctx.matrices.scale(scalef, scalef, 1.0f)
                ctx.matrices.translate(
                    (titleX / scalef).toDouble(),
                    (descY / scalef).toDouble(),
                    0.0
                )

                for ((index, chunk) in chunks.withIndex()) {
                    ctx.drawText(
                        this.client!!.textRenderer,
                        chunk,
                        0,
                        40 + 10 * index,
                        0xFFFFFF,
                        false
                    )
                }

                ctx.drawText(
                    this.client!!.textRenderer,
                    "Code name: ",
                    0,
                    268,
                    0xFFFFFF,
                    false
                )

                ctx.drawText(
                    this.client!!.textRenderer,
                    output.item.toString(),
                    0,
                    280,
                    0xFFFFFF,
                    false
                )

                ctx.matrices.pop()
            }
        }
    }

    private fun isMouseOverSlot(slot: Slot, mouseX: Int, mouseY: Int): Boolean {
        val aX = mouseX - this.x
        val aY = mouseY - this.y
        return aX >= slot.x && aX < slot.x + 18 && aY >= slot.y && aY < slot.y + 18
    }

    private fun loadDescriptions() {
        val fallback = CraftingTableIIFabric.id(
            "${DESCRIPTIONS_BASE_PATH}/en_us.json"
        )
        val resourceManager = MinecraftClient.getInstance().resourceManager
        val resource =
            resourceManager.getResource(this.descriptionsIdentifier).getOrNull()
                ?: resourceManager.getResource(fallback).getOrNull() ?: return
        val inputStream = resource.inputStream
        val json = inputStream.bufferedReader().use { it.readText() }
        this.descriptionsMap = Gson().fromJson(
            json, HashMap::class.java
        ) as HashMap<String, String>
    }

    private fun chunkDescription(description: String): List<String> {
        if (description.isEmpty()) return listOf("")

        val chunks = arrayListOf<String>()
        val sentences = description.split(". ")

        for (sentence in sentences) {
            val words = sentence.split(" ")
            var chunk = ""

            for (word in words) {
                if (chunk.length + word.length + 1 > 36) { // +1 to account for the period
                    chunks.add(chunk)
                    chunk = ""
                }
                chunk += "$word "
            }

            if (chunk.isNotBlank()) {
                chunks.add(chunk.trim() + ".") // add the period at the end of each chunk
            }
        }

        val last = chunks[chunks.size - 1]
        chunks[chunks.size - 1] = last.substring(
            0,
            last.length - 1
        ) // remove the period from the last chunk

        return chunks
    }
}

