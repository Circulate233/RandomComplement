package com.circulation.random_complement.common.util;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;

import java.util.Map;

public interface RCCraftingGridCache {

    Multiset<SimpleItem> rc$getCanCraftableItems();
    Map<IAEItemStack, ImmutableList<ICraftingPatternDetails>> rc$getCraftableItems();
}
