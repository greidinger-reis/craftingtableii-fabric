package net.johnpgr.craftingtableiifabric.renderer

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntityRenderer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos

class CraftingTableIIItemDynamicRenderer : BuiltinItemRendererRegistry.DynamicItemRenderer {
    companion object {
        fun register() {
            BuiltinItemRendererRegistry.INSTANCE.register(
                CraftingTableIIMod.BLOCK,
                CraftingTableIIItemDynamicRenderer()
            )
        }
    }

    override fun render(
        stack: ItemStack,
        mode: ModelTransformationMode,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        lightmap: Int,
        overlay: Int
    ) {
        val tableEntity = CraftingTableIIEntity(
            CraftingTableIIMod.BLOCK,
            BlockPos.ORIGIN,
            CraftingTableIIMod.BLOCK.defaultState
        )
        val instance = MinecraftClient.getInstance()

        val dummyRenderer = CraftingTableIIEntityRenderer(
            BlockEntityRendererFactory.Context(
                instance.blockEntityRenderDispatcher,
                instance.blockRenderManager,
                instance.itemRenderer,
                instance.entityRenderDispatcher,
                instance.entityModelLoader,
                instance.textRenderer
            )
        )
        dummyRenderer.render(
            tableEntity,
            instance.renderTickCounter.getTickDelta(true),
            matrixStack,
            vertexConsumerProvider,
            lightmap,
            overlay
        )
    }
}