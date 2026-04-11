package com.circulation.random_complement.mixin.ae2;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.me.cache.EnergyGridCache;
import com.circulation.random_complement.RCConfig;
import com.circulation.random_complement.common.interfaces.RCEnergyGridCache;
import com.circulation.random_complement.common.interfaces.RCGrid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnergyGridCache.class, remap = false)
public abstract class MixinEnergyGridCache implements RCEnergyGridCache {

    @Shadow
    @Final
    private IGrid myGrid;

    @Inject(method = "isNetworkPowered", at = @At("HEAD"), cancellable = true)
    public void isNetworkPowered(CallbackInfoReturnable<Boolean> cir) {
        if (RCConfig.AE2.debugEnergy || r$hasCreativeEnergyCell()) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    public boolean r$hasCreativeEnergyCell() {
        return ((RCGrid) this.myGrid).r$hasCreativeEnergyCell();
    }

    @Inject(method = "extractAEPower", at = @At("HEAD"), cancellable = true)
    public void extractAEPower(double amt, Actionable mode, PowerMultiplier pm, CallbackInfoReturnable<Double> cir) {
        if (RCConfig.AE2.debugEnergy || r$hasCreativeEnergyCell()) {
            cir.setReturnValue(amt);
        }
    }
}