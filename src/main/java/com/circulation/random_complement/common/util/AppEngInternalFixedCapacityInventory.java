package com.circulation.random_complement.common.util;

import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;

public class AppEngInternalFixedCapacityInventory extends AppEngInternalInventory {

    public AppEngInternalFixedCapacityInventory(IAEAppEngInventory inventory, int size) {
        super(inventory, size);
    }

    @Override
    public void setSize(int size) {
    }

}
