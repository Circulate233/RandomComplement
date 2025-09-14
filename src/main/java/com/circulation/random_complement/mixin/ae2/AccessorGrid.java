package com.circulation.random_complement.mixin.ae2;

import appeng.api.networking.IGridHost;
import appeng.me.Grid;
import appeng.me.MachineSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = Grid.class,remap = false)
public interface AccessorGrid {

    @Accessor("machines")
    Map<Class<? extends IGridHost>, MachineSet> r$getMachines();
}
