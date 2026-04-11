package com.circulation.random_complement.mixin.ae2;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.me.cache.EnergyGridCache;
import com.circulation.random_complement.common.interfaces.RCEnergyGridCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnergyGridCache.GridPowerStorage.class, remap = false)
public abstract class MixinGridPowerStorage {

    @Shadow @Final
    EnergyGridCache this$0;

    @Unique
    private boolean r$hasCreativeEnergyCell() {
        return ((RCEnergyGridCache) this.this$0).r$hasCreativeEnergyCell();
    }

    @Inject(method = "extractAEPower", at = @At("HEAD"), cancellable = true)
    public void extractAEPower(double amt, Actionable mode, PowerMultiplier pm, CallbackInfoReturnable<Double> cir) {
        if (r$hasCreativeEnergyCell()) {
            cir.setReturnValue(amt);
        }
    }
}