package com.circulation.random_complement.common.interfaces;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import com.circulation.random_complement.common.util.SimpleItem;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Map;

public interface RCCraftingGridCache {

    Collection<SimpleItem> rc$getCanCraftableItems();
    Map<IAEItemStack, ImmutableList<ICraftingPatternDetails>> rc$getCraftableItems();
    void rc$updatePatterns();
}
