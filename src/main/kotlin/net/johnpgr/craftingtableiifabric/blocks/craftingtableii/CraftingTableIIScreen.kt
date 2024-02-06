package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import com.mojang.blaze3d.systems.RenderSystem
import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
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
    var scrollPosition = 0.0f

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
            this.scrollCraftableRecipes()

            return true
        }

        return false
    }

    private fun scrollCraftableRecipes() {
        val craftableRecipesSize =
            this.screenHandler.recipeManager?.recipeItemStacks?.size ?: return
        val i = (craftableRecipesSize + 8 - 1) / 8 - 5
        var j = ((this.scrollPosition * i.toFloat()).toDouble() + 0.5).toInt()
        if (j < 0) {
            j = 0
        }

        var newCurrentFirstIndexInList = -1

        for (k in 0 until 5) {
            for (l in 0 until 8) {
                val listIndex = l + (k + j) * 8
                if (newCurrentFirstIndexInList == -1 && listIndex < craftableRecipesSize) {
                    newCurrentFirstIndexInList = listIndex
                    this.screenHandler.currentFirstRecipeIndexToDisplay =
                        newCurrentFirstIndexInList
                }

                var itemStack = ItemStack.EMPTY

                if (listIndex in 0..<craftableRecipesSize) {
                    itemStack =
                        this.screenHandler.recipeManager?.recipeItemStacks?.getOrElse(
                            listIndex
                        ) { ItemStack.EMPTY } ?: ItemStack.EMPTY
                }

                val slotIndex = l + k * 8
                val recipeSlot = this.screenHandler.getSlot(slotIndex + 10) // +10 because of the crafting + result inventory
                recipeSlot.stack = itemStack
            }
        }
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

        val k1 = y + 17
        val l1 = k1 + 88 + 2
        val craftableRecipesSize = this.screenHandler.recipeManager?.recipeItemStacks?.size ?: 0

        //draw scrollbar
        ctx.drawTexture(
            texture,
            x + 154,
            y + 17 + ((l1 - k1 - 17).toFloat() * scrollPosition).toInt(),
            if (craftableRecipesSize <= CraftingTableIIInventory.SIZE) 16 else 0,
            208,
            16,
            16
        )
    }
}