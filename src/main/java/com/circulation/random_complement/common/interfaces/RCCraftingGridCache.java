package com.circulation.random_complement.common.interfaces;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import com.circulation.random_complement.common.crafting.RCCraftingSnapshot;
import com.google.common.collect.ImmutableList;

import java.util.Map;

public interface RCCraftingGridCache {

    Map<IAEItemStack, ImmutableList<ICraftingPatternDetails>> rc$getCraftableItems();

    RCCraftingSnapshot.Keys rc$peekTrimKeys(IAEItemStack output);

    void rc$cacheTrimKeys(IAEItemStack output, RCCraftingSnapshot.Keys keys);

    int rc$stockSize(IItemList<IAEItemStack> full, long time);
}
