package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import com.mojang.blaze3d.systems.RenderSystem
import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper

class CraftingTableIIScreen(
    screenHandler: CraftingTableIIScreenHandler,
    player: PlayerEntity,
    title: Text
) : HandledScreen<CraftingTableIIScreenHandler>(
    screenHandler, player.inventory, title
) {
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
            if (slot is CraftingTableIISlot && isMouseOverSlot(
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
                    161
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

                recipeStacks.forEachIndexed { i, stack ->
                    ctx.drawItem(
                        stack,
                        x - 25,
                        y + 5 + i * 18
                    )
                    ctx.drawItemInSlot(
                        this.client!!.textRenderer,
                        stack,
                        x - 25,
                        y + 5 + i * 18
                    )
                }

                val output =
                    recipe.getOutput(this.client!!.world!!.registryManager)

                ctx.drawText(
                    this.client!!.textRenderer,
                    output.name,
                    x - 119,
                    y + 5,
                    0xFFFFFF,
                    false,
                )
            }
        }
    }

    private fun isMouseOverSlot(slot: Slot, mouseX: Int, mouseY: Int): Boolean {
        val aX = mouseX - this.x
        val aY = mouseY - this.y
        return aX >= slot.x && aX < slot.x + 18 && aY >= slot.y && aY < slot.y + 18
    }
}