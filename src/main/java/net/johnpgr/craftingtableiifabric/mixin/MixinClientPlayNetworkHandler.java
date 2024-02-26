package net.johnpgr.craftingtableiifabric.mixin;

import net.johnpgr.craftingtableiifabric.blocks.craftingtableii.CraftingTableIIScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Inject(at = @At("RETURN"), method = "onUnlockRecipes")
    private void onUnlockRecipes(UnlockRecipesS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        if (client.player.currentScreenHandler instanceof CraftingTableIIScreenHandler) {
            ((CraftingTableIIScreenHandler) client.player.currentScreenHandler).updateRecipes();
        }
    }
}
