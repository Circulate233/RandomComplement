package com.circulation.random_complement.mixin.ae2;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.crafting.CraftingJob;
import appeng.crafting.CraftingTreeNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CraftingTreeNode.class, remap = false)
public interface AccessorCraftingTreeNode {
    @Accessor
    IItemList<IAEItemStack> getUsed();

    @Accessor
    long getHowManyEmitted();

    @Accessor
    void setHowManyEmitted(long howManyEmitted);

    @Accessor
    boolean isCanEmit();

    @Accessor
    CraftingJob getJob();
}
