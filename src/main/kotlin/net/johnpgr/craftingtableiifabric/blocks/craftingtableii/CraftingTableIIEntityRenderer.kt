package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis

class CraftingTableIIEntityRenderer(private val arg: BlockEntityRendererFactory.Context) :
    BlockEntityRenderer<CraftingTableIIEntity> {
    private val table = arg.getLayerModelPart(CraftingTableIIEntityModel.tableModelLayer)
    private val door = arg.getLayerModelPart(CraftingTableIIEntityModel.doorModelLayer)
    private val doorSide = arg.getLayerModelPart(CraftingTableIIEntityModel.doorSideModelLayer)
    private val doorSide1 = arg.getLayerModelPart(CraftingTableIIEntityModel.doorSide1ModelLayer)
    private val doorTopSide = arg.getLayerModelPart(CraftingTableIIEntityModel.doorTopSideModelLayer)
    private val doorTopSide1 = arg.getLayerModelPart(CraftingTableIIEntityModel.doorTopSide1ModelLayer)
    private val book = arg.getLayerModelPart(CraftingTableIIEntityModel.bookModelLayer)

    override fun render(
        entity: CraftingTableIIEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val spriteId = SpriteIdentifier(
            PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
            Identifier(CraftingTableIIFabric.MOD_ID + ":block/craftingtableii")
        )
        val consumer = spriteId.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout)
        val rotation = entity.cachedState.get(Properties.HORIZONTAL_FACING).asRotation() * 89f
        val lightAbove = WorldRenderer.getLightmapCoordinates(entity.world, entity.cachedState, entity.pos.up())

        matrices.push()
        matrices.translate(0.5, 1.0, 0.5)
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(-rotation))
        matrices.scale(-1f, -1f, 1f)

        this.renderModels(entity.doorAngle, matrices, consumer, lightAbove, overlay)

        matrices.pop()
    }

    private fun renderModels(
        rotation: Float,
        matrices: MatrixStack,
        consumer: VertexConsumer,
        light: Int,
        overlay: Int
    ) {
        this.door.getChild("door").setAngles(0f, rotation, 0f)
        this.table.render(matrices, consumer, light, overlay)
        this.door.render(matrices, consumer, light, overlay)
        this.doorSide.render(matrices, consumer, light, overlay)
        this.doorSide1.render(matrices, consumer, light, overlay)
        this.doorTopSide.render(matrices, consumer, light, overlay)
        this.doorTopSide1.render(matrices, consumer, light, overlay)
        this.book.render(matrices, consumer, light, overlay)
    }
}