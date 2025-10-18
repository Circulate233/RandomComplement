package com.circulation.random_complement.mixin.ae2;

import appeng.me.helpers.AENetworkProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AENetworkProxy.class, remap = false)
public interface AccessorAENetworkProxy {

    @Accessor("idleDraw")
    void r$setIdleDraw(double newValue);

}
