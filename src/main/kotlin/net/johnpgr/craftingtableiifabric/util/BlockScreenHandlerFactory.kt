package net.johnpgr.craftingtableiifabric.util

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.johnpgr.craftingtableiifabric.entity.CraftingTableIIEntity
import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

class BlockScreenHandlerFactory(val block: Block, val pos: BlockPos) : ExtendedScreenHandlerFactory {
    companion object {
        fun createHandlerType() = ExtendedScreenHandlerType { i, playerInventory, packetByteBuf ->
            val pos = packetByteBuf.readBlockPos()
            val player = playerInventory.player
            val world = player.world
            val blockEntity = world.getBlockEntity(pos) as CraftingTableIIEntity
            CraftingTableIIScreenHandler(
                i, player, blockEntity, ScreenHandlerContext.create(world, pos)
            )
        }
    }

    override fun createMenu(
        syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity
    ): ScreenHandler {
        val world = player.world
        val blockEntity = world.getBlockEntity(pos) as CraftingTableIIEntity
        return CraftingTableIIScreenHandler(
            syncId, player, blockEntity, ScreenHandlerContext.create(world, pos)
        )
    }

    private val displayName: Text = Text.translatable(
        "screen.${CraftingTableIIMod.MOD_ID}.${
            Registries.BLOCK.getId(block).path
        }"
    )

    override fun writeScreenOpeningData(
        player: ServerPlayerEntity?, buf: PacketByteBuf?
    ) {
        buf?.writeBlockPos(pos)
    }

    override fun getDisplayName() = displayName
}