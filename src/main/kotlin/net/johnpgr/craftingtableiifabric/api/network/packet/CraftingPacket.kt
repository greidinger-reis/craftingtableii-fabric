package net.johnpgr.craftingtableiifabric.api.network.packet

import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeManager

class CraftingPacket(val recipe: Recipe<*>) {
    companion object {
        fun read(
            buf: PacketByteBuf,
            recipeManager: RecipeManager
        ): CraftingPacket? {
            val id = buf.readIdentifier()
            val recipe = recipeManager.get(id)
            if (recipe.isPresent) {
                return CraftingPacket(recipe.get())
            }
            return null
        }
    }

    fun write(buf: PacketByteBuf) {
        buf.writeIdentifier(recipe.id)
    }
}

