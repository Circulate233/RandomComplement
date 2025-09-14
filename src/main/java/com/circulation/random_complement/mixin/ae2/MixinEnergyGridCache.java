package com.circulation.random_complement.mixin.ae2;

import appeng.api.networking.IGrid;
import appeng.me.cache.EnergyGridCache;
import appeng.tile.networking.TileCreativeEnergyCell;
import com.circulation.random_complement.RCConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnergyGridCache.class,remap = false)
public abstract class MixinEnergyGridCache {

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
    private boolean r$hasCreativeEnergyCell(){
        if (((AccessorGrid) this.myGrid).r$getMachines().containsKey(TileCreativeEnergyCell.class)){
            return !this.myGrid.getMachines(TileCreativeEnergyCell.class).isEmpty();
        }
        return false;
    }
}