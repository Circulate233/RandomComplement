package com.circulation.random_complement.mixin.packagedauto;

import mezz.jei.bookmarks.BookmarkItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thelm.packagedauto.integration.jei.EncoderGhostIngredientHandler;

@Mixin(value = EncoderGhostIngredientHandler.class, remap = false)
public abstract class MixinEncoderGhostIngredientHandler {

    @Shadow
    private static ItemStack wrapStack(Object ingredient) {
        return null;
    }

    @Inject(method = "wrapStack", at = @At("HEAD"), cancellable = true)
    private static void wrapStackMixin(Object ingredient, CallbackInfoReturnable<ItemStack> cir) {
        if (ingredient instanceof BookmarkItem<?> b) {
            cir.setReturnValue(wrapStack(b.ingredient));
        }
    }
}
