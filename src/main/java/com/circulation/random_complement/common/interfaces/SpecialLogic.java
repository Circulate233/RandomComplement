package com.circulation.random_complement.common.interfaces;

import com.circulation.random_complement.client.CraftableItem;

import java.util.Set;

public interface SpecialLogic {

    Set<CraftableItem> r$getList();

    void r$setList(Set<CraftableItem> list);

    void r$addList(CraftableItem item);

    void r$addAllList(Set<CraftableItem> list);
}
