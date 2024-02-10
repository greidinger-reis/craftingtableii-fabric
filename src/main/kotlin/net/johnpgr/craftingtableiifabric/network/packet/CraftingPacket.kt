package net.johnpgr.craftingtableiifabric.network.packet

import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class CraftingPacket(
    val recipe: Identifier,
    val syncId: Int,
    val quickCraft: Boolean
) {
    companion object {
        fun read(
            buf: PacketByteBuf,
        ): CraftingPacket {
            val recipe = buf.readIdentifier()
            val syncId = buf.readInt()
            val quickCraft = buf.readBoolean()

            return CraftingPacket(recipe, syncId, quickCraft)
        }
    }

    fun write(buf: PacketByteBuf) {
        buf.writeIdentifier(this.recipe)
        buf.writeInt(this.syncId)
        buf.writeBoolean(this.quickCraft)
    }
}

