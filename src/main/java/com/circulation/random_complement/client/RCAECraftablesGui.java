package com.circulation.random_complement.client;

import appeng.api.storage.data.IAEItemStack;

import java.util.Collection;
import java.util.Set;

public interface RCAECraftablesGui {

    Set<IAEItemStack> r$getCpuCache();

    void r$addCpuCache(Collection<IAEItemStack> list);

    Set<IAEItemStack> r$getCraftablesCache();

    default void r$addCraftablesCache(Collection<IAEItemStack> list) {

    }
}