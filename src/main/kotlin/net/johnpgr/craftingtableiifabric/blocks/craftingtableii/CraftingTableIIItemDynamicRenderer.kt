package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos

class CraftingTableIIItemDynamicRenderer : BuiltinItemRendererRegistry.DynamicItemRenderer {
    override fun render(
        stack: ItemStack,
        mode: ModelTransformationMode,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        lightmap: Int,
        overlay: Int
    ) {
        val tableEntity = CraftingTableIIEntity(
            ModBlocks.CRAFTING_TABLE_II as CraftingTableII,
            BlockPos.ORIGIN,
            ModBlocks.CRAFTING_TABLE_II.defaultState
        )

        val dummyRenderer = CraftingTableIIEntityRenderer(
            BlockEntityRendererFactory.Context(
                MinecraftClient.getInstance().blockEntityRenderDispatcher,
                MinecraftClient.getInstance().blockRenderManager,
                MinecraftClient.getInstance().itemRenderer,
                MinecraftClient.getInstance().entityRenderDispatcher,
                MinecraftClient.getInstance().entityModelLoader,
                MinecraftClient.getInstance().textRenderer
            )
        )
        dummyRenderer.render(
            tableEntity,
            MinecraftClient.getInstance().tickDelta,
            matrixStack,
            vertexConsumerProvider,
            lightmap,
            overlay
        )
    }
}