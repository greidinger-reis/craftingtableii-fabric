package net.johnpgr.craftingtableiifabric.screens

import com.mojang.blaze3d.systems.RenderSystem
import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.johnpgr.craftingtableiifabric.screens.handlers.CraftingTableIIScreenHandler
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class CraftingTableIIScreen(
    handler: CraftingTableIIScreenHandler,
    playerInventory: PlayerInventory,
    title: Text
) : HandledScreen<CraftingTableIIScreenHandler>(
    handler,
    playerInventory,
    title
) {
    companion object {
        private val TEXTURE =
            CraftingTableIIFabric.id(
                "textures/gui/crafttableii.png"
            )
    }

    override fun drawBackground(
        ctx: DrawContext,
        delta: Float,
        mouseX: Int,
        mouseY: Int
    ) {
        renderBackground(ctx)
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)

        ctx.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight)
    }

    override fun render(
        context: DrawContext,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    override fun init() {
        super.init()
        backgroundHeight = 230
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 11
        playerInventoryTitleY = backgroundHeight - 119
        x = width / 2 - backgroundWidth / 2
        y = height / 2 - backgroundHeight / 2
    }
}