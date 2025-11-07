package com.circulation.random_complement.mixin.ae2;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.tile.networking.TileCreativeEnergyCell;
import com.circulation.random_complement.common.interfaces.RCGridPowerStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "appeng.me.cache.EnergyGridCache$GridPowerStorage", remap = false)
public abstract class MixinGridPowerStorage implements RCGridPowerStorage {

    @Unique
    private IGrid r$grid;

    @Unique
    private boolean r$hasCreativeEnergyCell() {
        if (r$grid == null) return false;
        final var m = ((AccessorGrid) this.r$grid).r$getMachines().get(TileCreativeEnergyCell.class);
        return m != null && !m.isEmpty();
    }

    @Inject(method = "extractAEPower", at = @At("HEAD"), cancellable = true)
    public void extractAEPower(double amt, Actionable mode, PowerMultiplier pm, CallbackInfoReturnable<Double> cir) {
        if (r$hasCreativeEnergyCell()) {
            cir.setReturnValue(amt);
        }
    }

    @Override
    public void r$setGrid(IGrid grid) {
        r$grid = grid;
    }
}