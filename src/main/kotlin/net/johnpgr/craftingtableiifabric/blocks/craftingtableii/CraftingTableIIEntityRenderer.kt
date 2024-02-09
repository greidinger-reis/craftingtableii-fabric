package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
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
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis

class CraftingTableIIEntityRenderer(private val arg: BlockEntityRendererFactory.Context) :
    BlockEntityRenderer<CraftingTableIIEntity> {
    private val table =
        arg.getLayerModelPart(CraftingTableIIEntityModel.tableModelLayer)
    private val door =
        arg.getLayerModelPart(CraftingTableIIEntityModel.doorModelLayer)
    private val doorSide =
        arg.getLayerModelPart(CraftingTableIIEntityModel.doorSideModelLayer)
    private val doorSide1 =
        arg.getLayerModelPart(CraftingTableIIEntityModel.doorSide1ModelLayer)
    private val doorTopSide =
        arg.getLayerModelPart(CraftingTableIIEntityModel.doorTopSideModelLayer)
    private val doorTopSide1 =
        arg.getLayerModelPart(CraftingTableIIEntityModel.doorTopSide1ModelLayer)
    private val book =
        arg.getLayerModelPart(CraftingTableIIEntityModel.bookModelLayer)

    override fun render(
        entity: CraftingTableIIEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val doorRotation = entity.doorAngle
        val blockState =
            if (entity.hasWorld()) entity.cachedState
            else ModBlocks.CRAFTING_TABLE_II.defaultState.with(
                Properties.HORIZONTAL_FACING,
                Direction.SOUTH
            )
        val blockRotation = blockState.get(Properties.HORIZONTAL_FACING)
            .asRotation() * 90f

        matrices.push()
        matrices.translate(0.5, 1.0, 0.5)
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-blockRotation))
        matrices.scale(-1f, -1f, 1f)

        val tableIdentifier = SpriteIdentifier(
            PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier(
                CraftingTableIIFabric.MOD_ID + ":block/craftingtableii"
            )
        )
        val tableConsumer = tableIdentifier.getVertexConsumer(
            vertexConsumers,
            RenderLayer::getEntityCutout
        )
        val lightAbove = entity.world.let { world ->
            WorldRenderer.getLightmapCoordinates(world, blockState, entity.pos)
        }

        this.renderModels(
            doorRotation,
            matrices,
            tableConsumer,
            255,
            overlay
        )

        matrices.pop()
    }

    private fun resetModelAngles() {
        this.door.setAngles(0.0f, 0.0f, 0.0f)
        this.doorSide.setAngles(0.0f, 0.0f, 0.0f)
        this.doorSide1.setAngles(0.0f, 0.0f, 0.0f)
        this.doorTopSide.setAngles(0.0f, 0.0f, 0.0f)
        this.doorTopSide1.setAngles(0.0f, 0.0f, 0.0f)
        this.book.setAngles(0.0f, 0.0f, 0.0f)
    }

    private fun renderModels(
        rotation: Float,
        matrices: MatrixStack,
        tableConsumer: VertexConsumer,
        lightAbove: Int,
        overlay: Int
    ) {
        this.resetModelAngles()

        this.table.render(
            matrices,
            tableConsumer,
            lightAbove,
            overlay
        )

        this.door.setAngles(0.0f, rotation, 0.0f)
        this.door.render(
            matrices,
            tableConsumer,
            lightAbove,
            overlay
        )

        this.doorSide.render(
            matrices,
            tableConsumer,
            lightAbove,
            overlay
        )

        this.doorSide1.render(
            matrices,
            tableConsumer,
            lightAbove,
            overlay
        )

        this.doorTopSide.render(
            matrices,
            tableConsumer,
            lightAbove,
            overlay
        )

        this.doorTopSide1.render(
            matrices,
            tableConsumer,
            lightAbove,
            overlay
        )

        this.book.render(
            matrices,
            tableConsumer,
            lightAbove,
            overlay
        )
    }
}