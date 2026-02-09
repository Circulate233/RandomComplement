package com.circulation.random_complement.mixin.ae2.container;

import appeng.api.networking.IGrid;
import appeng.container.implementations.ContainerCraftingCPU;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ContainerCraftingCPU.class, remap = false)
public interface AccessorContainerCraftingCPU {

    @Invoker("getMonitor")
    CraftingCPUCluster invokerGetMonitor();

    @Invoker("getNetwork")
    IGrid invokerGetNetwork();
}
