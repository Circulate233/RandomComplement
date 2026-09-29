package com.circulation.random_complement.mixin.ae2.recursive;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.crafting.CraftingTreeNode;
import com.circulation.random_complement.RCConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CraftingTreeNode.class, remap = false)
public class MixinRecursiveCraftingTreeNode {

    @Shadow
    @Final
    private IAEItemStack what;

    @Shadow
    private boolean canEmit;

    @Inject(method = "notRecursive", at = @At("RETURN"), cancellable = true)
    public void rc$notRecursive(ICraftingPatternDetails details, CallbackInfoReturnable<Boolean> cir) {
        if (!RCConfig.AE2.recursiveCraftingOptimization) return;
        if (canEmit) return;
        if (cir.getReturnValueZ()) {
            for (var input : details.getCondensedInputs()) {
                if (this.what.equals(input)) {
                    cir.setReturnValue(Boolean.FALSE);
                    break;
                }
            }
        }
    }
}
