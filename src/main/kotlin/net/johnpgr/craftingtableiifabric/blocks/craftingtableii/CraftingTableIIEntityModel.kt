package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModelLayer

object CraftingTableIIEntityModel {
    private const val TEXTURE_WIDTH = 128
    private const val TEXTURE_HEIGHT = 64

    val tableModelLayer = EntityModelLayer(
        CraftingTableIIFabric.id("craftingtableii"),
        "table"
    )
    val doorModelLayer = EntityModelLayer(
        CraftingTableIIFabric.id("craftingtableii"),
        "door"
    )
    val doorSideModelLayer = EntityModelLayer(
        CraftingTableIIFabric.id("craftingtableii"),
        "door_side"
    )
    val doorSide1ModelLayer = EntityModelLayer(
        CraftingTableIIFabric.id("craftingtableii"),
        "door_side1"
    )
    val doorTopSideModelLayer = EntityModelLayer(
        CraftingTableIIFabric.id("craftingtableii"),
        "door_top_sider"
    )
    val doorTopSide1ModelLayer = EntityModelLayer(
        CraftingTableIIFabric.id("craftingtableii"),
        "door_top_side1"
    )
    val bookModelLayer = EntityModelLayer(
        CraftingTableIIFabric.id("craftingtableii"),
        "book"
    )

    private fun setupTable(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "table",
            ModelPartBuilder
                .create()
                .uv(0, 0)
                .cuboid(-7f, 0f, -7f, 9f, 16f, 14f)
                .mirrored(true),
            ModelTransform.NONE
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun setupDoor(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "door",
            ModelPartBuilder
                .create()
                .uv(96, 0)
                .cuboid(0f, 0f, 0f, 1f, 16f, 13f)
                .mirrored(true),
            ModelTransform.pivot(0f, 0f, 0f)
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun setupDoorSide(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "door_side",
            ModelPartBuilder
                .create()
                .uv(61, 0)
                .cuboid(0f, 0f, 0f, 4f, 16f, 1f)
                .mirrored(true),
            ModelTransform.pivot(2f, 0f, -7f)
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun setupDoorSide1(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "door_side1",
            ModelPartBuilder
                .create()
                .uv(71, 0)
                .cuboid(0f, 0f, 0f, 4f, 16f, 1f)
                .mirrored(true),
            ModelTransform.pivot(2f, 0f, 6f)
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun setupDoorTopSide(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "door_top_side",
            ModelPartBuilder
                .create()
                .uv(0, 46)
                .cuboid(0f, 0f, 0f, 4f, 1f, 12f)
                .mirrored(true),
            ModelTransform.pivot(2f, 0f, -6f)
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun setupDoorTopSide1(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "door_top_side1",
            ModelPartBuilder
                .create()
                .uv(0, 33)
                .cuboid(0f, 0f, 0f, 4f, 1f, 12f)
                .mirrored(true),
            ModelTransform.pivot(2f, 15f, -5.8f)
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun setupBook(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "book",
            ModelPartBuilder
                .create()
                .uv(61, 23)
                .cuboid(0f, 0f, 0f, 5f, 1f, 3f)
                .mirrored(true),
            ModelTransform.rotation(-2f, -1f, 3.5f)
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    fun getEntries(): LinkedHashMap<EntityModelLayer, TexturedModelData> {
        val map = linkedMapOf<EntityModelLayer, TexturedModelData>()
        map[tableModelLayer] = setupTable()
        map[doorModelLayer] = setupDoor()
        map[doorSideModelLayer] = setupDoorSide()
        map[doorSide1ModelLayer] = setupDoorSide1()
        map[doorTopSideModelLayer] = setupDoorTopSide()
        map[doorTopSide1ModelLayer] = setupDoorTopSide1()
        map[bookModelLayer] = setupBook()
        return map
    }

}
