package com.circulation.random_complement.mixin.ae2.container;

import appeng.container.AEBaseContainer;
import appeng.container.guisync.SyncData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(AEBaseContainer.class)
public interface AccessorAEBaseContainer {
    @Accessor(remap = false)
    HashMap<Integer, SyncData> getSyncData();

    @Accessor(remap = false)
    @Mutable
    void setSyncData(HashMap<Integer, SyncData> daya);
}
