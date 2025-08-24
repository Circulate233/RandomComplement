package com.circulation.random_complement.common.interfaces;

import appeng.client.me.ItemRepo;
import com.circulation.random_complement.common.util.SimpleItem;

import java.util.Set;

public interface SpecialLogic {

    Set<SimpleItem> r$getList();

    void r$setList(Set<SimpleItem> list);

    void r$addList(SimpleItem item);

    void r$addAllList(Set<SimpleItem> list);

    ItemRepo r$getRepo();

    default boolean r$notMonitorable(){
        return r$getRepo() == null;
    }
}
