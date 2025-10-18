package com.circulation.random_complement.mixin.ae2;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.me.cache.EnergyGridCache;
import appeng.tile.networking.TileCreativeEnergyCell;
import com.circulation.random_complement.RCConfig;
import com.circulation.random_complement.common.interfaces.RCGridPowerStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(value = EnergyGridCache.class, remap = false)
public abstract class MixinEnergyGridCache {

    @Unique
    private static Field r$localStorage;

    static {
        try {
            r$localStorage = EnergyGridCache.class.getDeclaredField("localStorage");
        } catch (NoSuchFieldException ignored) {

        }
    }

    @Shadow
    @Final
    private IGrid myGrid;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(IGrid g, CallbackInfo ci) {
        if (r$localStorage != null) {
            try {
                RCGridPowerStorage rg = (RCGridPowerStorage) r$localStorage.get(this);
                rg.r$setGrid(g);
            } catch (IllegalAccessException ignored) {

            }
        }
    }

    @Inject(method = "isNetworkPowered", at = @At("HEAD"), cancellable = true)
    public void isNetworkPowered(CallbackInfoReturnable<Boolean> cir) {
        if (RCConfig.AE2.debugEnergy || r$hasCreativeEnergyCell()) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean r$hasCreativeEnergyCell() {
        if (((AccessorGrid) this.myGrid).r$getMachines().containsKey(TileCreativeEnergyCell.class)) {
            return !this.myGrid.getMachines(TileCreativeEnergyCell.class).isEmpty();
        }
        return false;
    }

    @Inject(method = "extractAEPower", at = @At("HEAD"), cancellable = true)
    public void extractAEPower(double amt, Actionable mode, PowerMultiplier pm, CallbackInfoReturnable<Double> cir) {
        if (RCConfig.AE2.debugEnergy || r$hasCreativeEnergyCell()) {
            cir.setReturnValue(amt);
        }
    }
}