package com.circulation.random_complement.mixin.nae2;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEStack;
import co.neeve.nae2.common.items.cells.vc.VoidCellInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VoidCellInventory.class, remap = false)
public class MixinVoidCellInventory<T extends IAEStack<T>> {

    @Inject(method = "injectItems", at = @At("RETURN"))
    public void injectItems(T input, Actionable mode, IActionSource src, CallbackInfoReturnable<T> cir) {
        if (mode == Actionable.MODULATE && cir.getReturnValue() == null) {
            input.setStackSize(0);
        }
    }

}