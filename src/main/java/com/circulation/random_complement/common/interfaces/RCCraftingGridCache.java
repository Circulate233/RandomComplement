package com.circulation.random_complement.common.interfaces;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import com.google.common.collect.ImmutableList;

import java.util.Map;

public interface RCCraftingGridCache {

    Map<IAEItemStack, ImmutableList<ICraftingPatternDetails>> rc$getCraftableItems();
}
