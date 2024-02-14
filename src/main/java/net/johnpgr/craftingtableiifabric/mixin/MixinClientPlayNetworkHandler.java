package net.johnpgr.craftingtableiifabric.mixin;

import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "onUnlockRecipes")
    private void onUnlockRecipes(UnlockRecipesS2CPacket packet, CallbackInfo ci){
        if(this.client.player.currentScreenHandler instanceof CraftingTableIIScreenHandler){
            ((CraftingTableIIScreenHandler)this.client.player.currentScreenHandler).updateRecipes();
        }
    }
}
