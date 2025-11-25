package com.circulation.random_complement.common.interfaces;

import appeng.api.storage.data.IAEItemStack;

public interface RCCraftingJob {

    IAEItemStack getWaitingItem();

    boolean isSpecialDeficiency();

    void setSpecialDeficiency(boolean b);

    boolean canIgnoredInput();
}
