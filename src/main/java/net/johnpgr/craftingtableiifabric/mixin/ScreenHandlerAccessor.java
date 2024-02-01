package net.johnpgr.craftingtableiifabric.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ScreenHandler.class)
public interface ScreenHandlerAccessor {
    @Accessor("trackedStacks")
    DefaultedList<ItemStack> getTrackedStacks();

    @Accessor("listeners")
    List<ScreenHandlerListener> getListeners();
}
