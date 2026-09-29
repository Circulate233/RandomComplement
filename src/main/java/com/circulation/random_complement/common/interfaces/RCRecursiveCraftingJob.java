package com.circulation.random_complement.common.interfaces;

import appeng.api.storage.data.IAEItemStack;

public interface RCRecursiveCraftingJob {

    IAEItemStack getWaitingItem();

    void setWaitingItem(IAEItemStack waiting);

    boolean isSpecialDeficiency();

    void setSpecialDeficiency(boolean b);

    boolean isMiss();

    void setMiss(boolean miss);
}
