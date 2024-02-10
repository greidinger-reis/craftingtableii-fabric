package net.johnpgr.craftingtableiifabric.utils

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.johnpgr.craftingtableiifabric.blocks.ModBlocks
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos

class BlockScreenHandlerFactory(val block: Block, val pos: BlockPos) :
    ExtendedScreenHandlerFactory {
    override fun createMenu(
        syncId: Int,
        playerInventory: PlayerInventory,
        player: PlayerEntity
    ): ScreenHandler {
        val world = player.world
        val blockEntity = world.getBlockEntity(pos)
        return ModBlocks.getContainerInfo(block)!!.screenHandlerClass.java.constructors[0].newInstance(
            syncId,
            player,
            blockEntity,
        ) as ScreenHandler
    }

    override fun writeScreenOpeningData(
        player: ServerPlayerEntity?,
        buf: PacketByteBuf?
    ) {
        buf?.writeBlockPos(pos)
    }

    override fun getDisplayName() = ModBlocks.getContainerInfo(block)!!.title
}