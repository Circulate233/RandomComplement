package com.circulation.random_complement.mixin.ae2.container;

import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.implementations.ContainerMEMonitorable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ContainerMEMonitorable.class,remap = false)
public interface AccessorContainerMEMonitorable {
    @Accessor
    IMEMonitor<IAEItemStack> getMonitor();
}
