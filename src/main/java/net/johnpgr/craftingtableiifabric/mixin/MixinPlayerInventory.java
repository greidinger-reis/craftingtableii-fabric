package net.johnpgr.craftingtableiifabric.mixin;

import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory {
    @Shadow
    @Final
    public PlayerEntity player;

    @Inject(at = @At("RETURN"), method = "markDirty")
    public void markDirty(CallbackInfo ci) {
        if (this.player.currentScreenHandler instanceof CraftingTableIIScreenHandler) {
            ((CraftingTableIIScreenHandler) this.player.currentScreenHandler).updateRecipes(true);
        }
    }
}
