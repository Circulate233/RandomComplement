package com.circulation.random_complement.common.interfaces;

public interface RCItemList {

    int RC$MAX_RESERVE = 4096;

    void rc$reserveCapacity(int expected);
}