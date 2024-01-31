package net.johnpgr.craftingtableiifabric.utils

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text

object CreativeTab {
    fun initCreativeTab() {
        Registry.register(Registries.ITEM_GROUP,
            CraftingTableIIFabric.id("creative_tab"),
            FabricItemGroup.builder()
                .icon { ItemStack(ModBlocks.CRAFTING_TABLE_II) }
                .displayName(Text.translatable("itemGroup.craftingtableiifabric.creative_tab"))
                .entries { _, entries -> entries.addAll(appendItems()) }
                .build())
    }

    private fun appendItems(): List<ItemStack> {
        return listOf(ItemStack(ModBlocks.CRAFTING_TABLE_II.asItem()))
    }
}