@file:Suppress("UNCHECKED_CAST")

package net.johnpgr.craftingtableiifabric.blocks

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.johnpgr.craftingtableiifabric.CraftingTableIIFabric
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Supplier
import kotlin.reflect.KClass

class ContainerInfo<T : ScreenHandler>(
    screenHandlerClass: KClass<*>,
    screenClass: Supplier<KClass<*>>,
    val identifier: Identifier? = null
) {
    val screenHandlerClass = screenHandlerClass as KClass<T>
    val screenClass = screenClass as Supplier<KClass<HandledScreen<T>>>
    var handlerType: ScreenHandlerType<T>? = null
    var handler: T? = null
    var title: Text = Text.literal("")

    fun init(blockIdentifier: Identifier) {
        val id = identifier ?: blockIdentifier
        title =
            Text.translatable("screen.${CraftingTableIIFabric.MOD_ID}.${id.path}")
        handlerType =
            ExtendedScreenHandlerType { i, playerInventory, packetByteBuf ->
                val pos = packetByteBuf.readBlockPos()
                val player = playerInventory.player
                val world = player.world
                val blockEntity = world.getBlockEntity(pos)
                handler = screenHandlerClass.java.constructors[0].newInstance(
                    i,
                    player,
                    blockEntity,
                ) as T
                handler
            }
        Registry.register(Registries.SCREEN_HANDLER, id, handlerType)
    }

    fun initClient() {
        HandledScreens.register(handlerType) { handler, playerInventory, title ->
            screenClass.get().java.constructors[0].newInstance(
                handler,
                playerInventory.player,
                title
            ) as HandledScreen<T>
        }
    }
}