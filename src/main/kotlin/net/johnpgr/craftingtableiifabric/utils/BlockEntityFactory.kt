package net.johnpgr.craftingtableiifabric.utils

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
            BlockEntityType.Builder.create({ blockPos, blockState ->
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