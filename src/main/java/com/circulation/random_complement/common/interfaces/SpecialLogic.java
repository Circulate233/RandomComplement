package com.circulation.random_complement.common.interfaces;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.me.ItemRepo;

import java.util.Collection;
import java.util.Set;

public interface SpecialLogic {

    Set<IAEItemStack> r$getList();

    void r$setList(Collection<IAEItemStack> list);

    void r$addAllList(Collection<IAEItemStack> list);

    default ItemRepo r$getRepo(){
        return null;
    }

    default boolean r$notMonitorable(){
        return r$getRepo() == null;
    }
}
