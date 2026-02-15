package com.circulation.random_complement.mixin.ae2;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.crafting.CraftingTreeNode;
import appeng.crafting.CraftingTreeProcess;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CraftingTreeProcess.class, remap = false)
public interface AccessorCraftingTreeProcess {

    @Accessor
    ICraftingPatternDetails getDetails();

    @Accessor
    Object2LongArrayMap<CraftingTreeNode> getNodes();
}
