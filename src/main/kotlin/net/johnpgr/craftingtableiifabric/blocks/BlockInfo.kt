@file:Suppress("UNCHECKED_CAST")

package net.johnpgr.craftingtableiifabric.blocks

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

class BlockInfo<T : BlockEntity>(
    val identifier: Identifier,
    private val block: Block,
    private val hasBlockItem: Boolean,
    private val blockItem: KClass<BlockItem>?,
    var entity: BlockEntityType<T>?,
    var renderer: KClass<BlockEntityRenderer<T>>?,
    var containers: List<ContainerInfo<*>>
) {
    fun init() {
        Registry.register(Registries.BLOCK, identifier, block)
        if (hasBlockItem) {
            if (blockItem != null) {
                Registry.register(
                    Registries.ITEM,
                    identifier,
                    blockItem.java.constructors[0].newInstance(
                        block,
                        FabricItemSettings()
                    ) as BlockItem
                )
            } else {
                Registry.register(
                    Registries.ITEM,
                    identifier,
                    BlockItem(block, FabricItemSettings())
                )
            }
            if(entity != null) {
                Registry.register(Registries.BLOCK_ENTITY_TYPE, identifier, entity)
            }
            containers.forEach { it.init(identifier) }
        }
    }

    fun initClient() {
        containers.forEach { it.initClient() }
        if(renderer != null) {
            BlockEntityRendererFactories.register(entity) {
                renderer!!.java.constructors[0].newInstance(it) as BlockEntityRenderer<T>
            }
        }
    }
}