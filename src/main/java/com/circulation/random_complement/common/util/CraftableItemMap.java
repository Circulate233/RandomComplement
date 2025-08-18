package com.circulation.random_complement.common.util;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.stream.Collectors;

public class CraftableItemMap extends Object2ObjectOpenHashMap<IAEItemStack, ImmutableList<ICraftingPatternDetails>> {

    private final Multiset<SimpleItem> outputs = HashMultiset.create();

    @Override
    public ImmutableList<ICraftingPatternDetails> put(IAEItemStack key, ImmutableList<ICraftingPatternDetails> value) {
        outputs.add(SimpleItem.getInstance(key));
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends IAEItemStack, ? extends ImmutableList<ICraftingPatternDetails>> map) {
        outputs.addAll(
            map.keySet()
                .stream()
                .map(SimpleItem::getInstance)
                .collect(Collectors.toList()));
        super.putAll(map);
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public ImmutableList<ICraftingPatternDetails> remove(Object key) {
        outputs.remove(key);
        return super.remove(key);
    }

    @Override
    public void clear() {
        outputs.clear();
        super.clear();
    }

    public Multiset<SimpleItem> getCanCraftableItems() {
        return ImmutableMultiset.copyOf(outputs);
    }
}
