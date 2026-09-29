package com.circulation.random_complement.common.interfaces;

public interface RCCraftingJob extends RCRecursiveCraftingJob {

    boolean canIgnoredInput();

    boolean isLock();

    void setLock(boolean lock);
}
