package com.circulation.random_complement.mixin.ae2;

import appeng.me.Grid;
import appeng.me.GridNode;
import appeng.tile.networking.TileCreativeEnergyCell;
import com.circulation.random_complement.common.interfaces.RCGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Grid.class, remap = false)
public class MixinGrid implements RCGrid {

    @Unique
    private int rc$ce = 0;

    @Inject(method = "add", at = @At("HEAD"))
    public void add(GridNode gridNode, CallbackInfo ci) {
        if (gridNode.getMachine() instanceof TileCreativeEnergyCell) {
            ++rc$ce;
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    public void remove(GridNode gridNode, CallbackInfo ci) {
        if (gridNode.getMachine() instanceof TileCreativeEnergyCell) {
            --rc$ce;
        }
    }

    @Override
    public boolean r$hasCreativeEnergyCell() {
        return rc$ce > 0;
    }
}
