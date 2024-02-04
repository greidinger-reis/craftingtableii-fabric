package net.johnpgr.craftingtableiifabric.api.network.packet

import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class CraftingPacket(
    val recipe: Identifier,
    val quickCraft: Boolean
) {
    companion object {
        fun read(
            buf: PacketByteBuf,
        ): CraftingPacket {
            val recipe = buf.readIdentifier()
            val quickCraft = buf.readBoolean()

            return CraftingPacket(recipe, quickCraft)
        }
    }

    fun write(buf: PacketByteBuf) {
        buf.writeIdentifier(this.recipe)
        buf.writeBoolean(this.quickCraft)
    }
}

