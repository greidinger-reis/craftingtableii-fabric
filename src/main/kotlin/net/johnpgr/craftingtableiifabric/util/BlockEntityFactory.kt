package net.johnpgr.craftingtableiifabric.util

import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType

object BlockEntityFactory {
    @Suppress("UNCHECKED_CAST")
    fun <T : BlockEntity> createEntityType(
        block: Block,
        apiRegistrations: (BlockEntityType<T>) -> Unit = {}
    ): BlockEntityType<T> {
        val ent = (block as BlockEntityProvider).let {
            FabricBlockEntityTypeBuilder.create({ blockPos, blockState ->
                block.createBlockEntity(
                    blockPos,
                    blockState,
                )
            }, block).build(null) as BlockEntityType<T>
        }
        apiRegistrations(ent)

        return ent
    }
}