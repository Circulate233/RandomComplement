package com.circulation.random_complement.mixin.ae2;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public interface AccessorCraftingCPUCluster {

    @Accessor
    Map<ICraftingPatternDetails, ?> getTasks();
}
