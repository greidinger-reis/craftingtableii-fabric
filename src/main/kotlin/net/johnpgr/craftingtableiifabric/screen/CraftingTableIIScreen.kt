package net.johnpgr.craftingtableiifabric.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.description.CraftingTableIIDescriptions
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIIInventory
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIISlot
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.render.GameRenderer
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper

class CraftingTableIIScreen(
    screenHandler: CraftingTableIIScreenHandler, playerInventory: PlayerInventory, title: Text
) : HandledScreen<CraftingTableIIScreenHandler>(
    screenHandler, playerInventory, title
) {
    companion object {
        fun register() {
            HandledScreens.register(
                CraftingTableIIMod.SCREEN_HANDLER, ::CraftingTableIIScreen
            )
        }
    }

    private val texture = CraftingTableIIMod.id(
        "textures/gui/crafttableii.png"
    )
    private val descriptionTexture = CraftingTableIIMod.id(
        "textures/gui/crafttableii_description.png"
    )
    private var scrolling = false
    private var scrollPosition = 0.0f
    private val scrollBarStartY: Int
        get() = y + 17

    private val scrollBarEndY: Int
        get() = scrollBarStartY + 90

    private val scrollButtonY: Int
        get() {
            val start = scrollBarStartY
            val end = scrollBarEndY

            return y + 17 + ((end - start - 17).toFloat() * scrollPosition).toInt()
        }

    private val scrollButtonX: Int
        get() = x + 154

    private fun hasScrollbar(): Boolean {
        val craftableRecipesSize = screenHandler.recipeManager.results.size
        return craftableRecipesSize > CraftingTableIIInventory.SIZE
    }

    private fun isClickInScrollbar(mouseX: Double, mouseY: Double): Boolean {
        return (mouseX in (scrollButtonX.toDouble()..(scrollButtonX + 16).toDouble())) && (mouseY in (scrollBarStartY.toDouble()..scrollBarEndY.toDouble()))
    }

    override fun init() {
        super.init()
        backgroundHeight = 208
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 11
        playerInventoryTitleY = backgroundHeight - 97
        x = width / 2 - backgroundWidth / 2
        y = height / 2 - backgroundHeight / 2
    }

    override fun render(
        context: DrawContext, mouseX: Int, mouseY: Int, delta: Float
    ) {
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    override fun mouseReleased(
        mouseX: Double, mouseY: Double, button: Int
    ): Boolean {
        if (button == 0) {
            scrolling = false
        }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseDragged(
        mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double
    ): Boolean {
        if (!scrolling) {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        }

        val start = scrollBarStartY
        val end = scrollBarEndY
        scrollPosition = (mouseY.toFloat() - start.toFloat() - 7.5f) / ((end - start).toFloat() - 15.0f)
        scrollPosition = MathHelper.clamp(scrollPosition, 0f, 1f)
        scrollResults(scrollPosition)
        return true
    }

    override fun mouseClicked(
        mouseX: Double, mouseY: Double, button: Int
    ): Boolean {
        if (scrolling || button != 0 || !isClickInScrollbar(
                mouseX, mouseY
            )
        ) {
            return super.mouseClicked(mouseX, mouseY, button)
        }

        scrolling = hasScrollbar()
        return true
    }

    /**
     * Scrolls through the list of craftable recipes based on the given scroll position.
     * Updates the current list index in the crafting screen handler and refreshes the displayed recipes.
     */
    private fun scrollResults(scrollPos: Float) {
        val results = screenHandler.recipeManager.results
        val i = (results.size + 8 - 1) / 8 - 5
        var j = ((scrollPos * i.toFloat()).toDouble() + 0.5).toInt()
        if (j < 0) {
            j = 0
        }

        val listIndex = j * 8
        if (listIndex < results.size) {
            screenHandler.currentListIndex = listIndex
        }

        screenHandler.updateRecipes(false)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        val craftableRecipesSize = screenHandler.recipeManager.results.size
        if (craftableRecipesSize <= CraftingTableIIInventory.SIZE) {
            return false
        }

        val aX = mouseX - x
        val aY = mouseY - y

        //check if the mouse is in our inventory bounds
        if ((aX >= 0 && aY >= 0 && aX < 176) && aY < backgroundHeight - 100) {
            val i = ((craftableRecipesSize + 8 - 1) / 8 - 5).toDouble()
            val j = MathHelper.clamp(amount, -1.0, 1.0)

            scrollPosition -= (j / i).toFloat()
            scrollPosition = MathHelper.clamp(scrollPosition, 0f, 1f)
            scrollResults(scrollPosition)

            return true
        }

        return false
    }

    override fun handledScreenTick() {
        super.handledScreenTick()
        screenHandler.tick()
    }

    override fun drawBackground(
        ctx: DrawContext, delta: Float, mouseX: Int, mouseY: Int
    ) {
        renderBackground(ctx)
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)

        //draw inventory
        ctx.drawTexture(
            texture, x, y, 0, 0, backgroundWidth, backgroundHeight
        )

        val craftableRecipesSize = screenHandler.recipeManager.results.size

        //draw scrollbar
        ctx.drawTexture(
            texture,
            scrollButtonX,
            scrollButtonY,
            if (craftableRecipesSize <= CraftingTableIIInventory.SIZE) 16 else 0,
            208,
            16,
            16
        )

        for (i in CraftingTableIIScreenHandler.CTII_INVENTORY_INDEX_START..CraftingTableIIScreenHandler.CTII_INVENTORY_INDEX_END) {
            val slot = screenHandler.getSlot(i) as? CraftingTableIISlot ?: continue

            if (isMouseOverSlot(slot, mouseX, mouseY)) {
                if (slot.stack.isEmpty) continue

                //draw description overlay
                ctx.drawTexture(descriptionTexture, x - 124, y, 0, 0, 121, 162)

                val recipe = slot.recipe
                val ingredientStacks = arrayListOf<ItemStack>()

                //TODO: Find a way to draw all matching stacks. Maybe a timer that loops through the list of matching stacks
                for (ingredient in recipe.ingredients) {
                    if (ingredient.isEmpty) continue

                    val item = ingredient.matchingStacks[0]
                    val index = ingredientStacks.indexOfFirst { it.item == item.item }

                    if (index == -1) {
                        ingredientStacks.add(item.copy())
                        continue
                    }
                    ingredientStacks[index].count += item.count
                }

                ingredientStacks.forEachIndexed { r, stack ->
                    ctx.drawItem(
                        stack, x - 25, y + 5 + r * 18
                    )
                    ctx.drawItemInSlot(
                        client!!.textRenderer, stack, x - 25, y + 5 + r * 18
                    )
                }

                val output = recipe.getOutput(client!!.world!!.registryManager)

                val titleX = x - 118
                val titleY = y + 9

                val title = if (output.name.string.length > 16) {
                    output.name.string.substring(0, 16) + "..."
                } else {
                    output.name.string
                }

                //draw title
                ctx.drawText(
                    client!!.textRenderer,
                    title,
                    titleX,
                    titleY,
                    0xFFFFFF,
                    false,
                )

                val description = CraftingTableIIDescriptions.descriptionsDict[output.translationKey] ?: ""
                val chunks = chunkDescription(description)
                val descY = titleY + 2
                val scalef = 0.5f

                ctx.matrices.push()
                ctx.matrices.scale(scalef, scalef, 1.0f)
                ctx.matrices.translate(
                    (titleX / scalef).toDouble(), (descY / scalef).toDouble(), 0.0
                )

                for ((index, chunk) in chunks.withIndex()) {
                    ctx.drawText(
                        client!!.textRenderer, chunk, 0, 40 + 10 * index, 0xFFFFFF, false
                    )
                }

                ctx.drawText(
                    client!!.textRenderer, "Code name: ", 0, 268, 0xFFFFFF, false
                )

                ctx.drawText(
                    client!!.textRenderer, output.item.toString(), 0, 280, 0xFFFFFF, false
                )

                ctx.matrices.pop()
            }
        }
    }

    private fun isMouseOverSlot(slot: Slot, mouseX: Int, mouseY: Int): Boolean {
        val aX = mouseX - x
        val aY = mouseY - y
        return aX >= slot.x && aX < slot.x + 18 && aY >= slot.y && aY < slot.y + 18
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
            0, last.length - 1
        ) // remove the period from the last chunk

        return chunks
    }
}
