package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import com.mojang.blaze3d.systems.RenderSystem
import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class CraftingTableIIScreen(
    screenHandler: CraftingTableIIScreenHandler,
    playerInventory: PlayerInventory,
    title: Text
) : HandledScreen<CraftingTableIIScreenHandler>(
    screenHandler,
    playerInventory,
    title
) {
    private val texture =
        CraftingTableIIFabric.id(
            "textures/gui/crafttableii.png"
        )

    override fun init() {
        super.init()
        backgroundHeight = 230
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 11
        playerInventoryTitleY = backgroundHeight - 119
        x = width / 2 - backgroundWidth / 2
        y = height / 2 - backgroundHeight / 2
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

    override fun drawBackground(
        ctx: DrawContext,
        delta: Float,
        mouseX: Int,
        mouseY: Int
    ) {
        renderBackground(ctx)
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)

        ctx.drawTexture(texture, x, y, 0, 0, backgroundWidth, backgroundHeight)
    }
}