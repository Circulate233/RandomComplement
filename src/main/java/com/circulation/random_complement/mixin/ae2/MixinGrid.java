package com.circulation.random_complement.mixin.ae2;

import appeng.me.Grid;
import appeng.me.GridNode;
import appeng.tile.networking.TileCreativeEnergyCell;
import com.circulation.random_complement.common.interfaces.RCGrid;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Grid.class,remap = false)
public class MixinGrid implements RCGrid {

    @Unique
    private ObjectSet<TileCreativeEnergyCell> rc$ce = new ObjectOpenHashSet<>();

    @Inject(method = "add",at = @At("HEAD"))
    public void add(GridNode gridNode, CallbackInfo ci) {
        if (gridNode.getMachine() instanceof TileCreativeEnergyCell t) {
            rc$ce.add(t);
        }
    }

    @Inject(method = "remove",at = @At("HEAD"))
    public void remove(GridNode gridNode, CallbackInfo ci) {
        if (gridNode.getMachine() instanceof TileCreativeEnergyCell t) {
            rc$ce.remove(t);
        }
    }

    @Override
    public boolean r$hasCreativeEnergyCell() {
        return rc$ce.isEmpty();
    }
}
