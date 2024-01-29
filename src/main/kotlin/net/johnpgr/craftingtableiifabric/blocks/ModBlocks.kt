package net.johnpgr.craftingtableiifabric.blocks

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.johnpgr.craftingtableiifabric.blocks.entities.CraftingTableIIBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModBlocks {
    val CRAFTING_TABLE_II = registerBlock(
        "craftingtableii",
        CraftingTableIIBlock(FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE))
    )
    val CRAFTING_TABLE_II_ENTITY = registerBlockEntity(
        "craftingtableii_entity",
        ::CraftingTableIIBlockEntity
    )

    private fun <T : BlockEntity?> registerBlockEntity(
        name: String,
        factory: FabricBlockEntityTypeBuilder.Factory<T>
    ): BlockEntityType<T> {
        return Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            CraftingTableIIFabric.id(name),
            FabricBlockEntityTypeBuilder.create(factory, CRAFTING_TABLE_II)
                .build()
        )
    }

    private fun registerBlock(name: String, block: Block): Block {
        registerBlockItem(name, block)

        return Registry.register(
            Registries.BLOCK,
            CraftingTableIIFabric.id(name),
            block
        )
    }

    private fun registerBlockItem(name: String, block: Block): Item {
        return Registry.register(
            Registries.ITEM,
            CraftingTableIIFabric.id(name),
            BlockItem(block, FabricItemSettings())
        )
    }

    //TODO: Create mod own item group
    fun registerBlockItemGroups() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
            .register { entries ->
                entries.add(
                    CRAFTING_TABLE_II,
                )
            }
    }
}

