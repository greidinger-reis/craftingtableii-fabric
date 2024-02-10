@file:Suppress("UNCHECKED_CAST")

package net.johnpgr.craftingtableiifabric.blocks

import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.*
import net.johnpgr.craftingtableiifabric.utils.FabricLoader
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.item.BlockItem
import net.minecraft.util.Identifier
import java.util.function.Supplier
import kotlin.reflect.KClass

object ModBlocks {
    private val blockRegistry = linkedMapOf<Block, BlockInfo<*>>()
    fun getBlockId(block: Block) = blockRegistry[block]?.identifier
    fun getEntityType(block: Block) =
        blockRegistry[block]?.entity as BlockEntityType<BlockEntity>

    fun getContainerInfo(block: Block) =
        blockRegistry[block]?.containers?.get(0)

    fun getContainerInfo(
        block: Block,
        identifier: Identifier
    ): ContainerInfo<*>? {
        blockRegistry[block]?.containers?.forEach {
            if (it.identifier == identifier) {
                return it
            }
        }
        return null
    }

    private fun register(
        identifier: Identifier,
        block: Block,
        hasBlockItem: Boolean = true
    ): Block {
        val info = BlockInfo<BlockEntity>(
            identifier,
            block,
            hasBlockItem,
            null,
            null,
            null,
            listOf()
        )
        blockRegistry[block] = info
        return block
    }

    private fun <T : BlockEntity> registerWithEntity(
        identifier: Identifier,
        block: Block,
        hasBlockItem: Boolean = true,
        blockItem: KClass<*>? = null,
        renderer: Supplier<KClass<*>>? = null,
        containers: List<ContainerInfo<*>> = listOf(),
        apiRegistrations: (BlockEntityType<T>) -> Unit = {}
    ): Block {
        val bli = blockItem as? KClass<BlockItem>
        val ent = (block as? BlockEntityProvider)?.let {
            BlockEntityType.Builder.create({ blockPos, blockState ->
                block.createBlockEntity(
                    blockPos,
                    blockState,
                )
            }, block).build(null) as BlockEntityType<T>
        }
        ent?.let { apiRegistrations(it) }
        val rnd =
            if (FabricLoader.isClient()) renderer?.let { it.get() as KClass<BlockEntityRenderer<T>> }
            else null
        val info =
            BlockInfo(
                identifier,
                block,
                hasBlockItem,
                bli,
                ent,
                rnd,
                containers
            )
        blockRegistry[block] = info
        return block
    }

    fun initBlocks() {
        blockRegistry.forEach { it.value.init() }
    }

    fun initBlocksClient() {
        blockRegistry.forEach { it.value.initClient() }
    }

    val CRAFTING_TABLE_II = registerWithEntity<CraftingTableIIEntity>(
        CraftingTableIIFabric.id("craftingtableii"),
        CraftingTableII(),
        containers = listOf(
            ContainerInfo<CraftingTableIIScreenHandler>(
                CraftingTableIIScreenHandler::class,
                { CraftingTableIIScreen::class }
            )
        ),
        renderer = { CraftingTableIIEntityRenderer::class }
    )
}

