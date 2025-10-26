package com.circulation.random_complement.client;

import appeng.container.slot.SlotFakeCraftingMatrix;
import net.minecraftforge.items.IItemHandler;

public class RCSlotFakeCraftingMatrix extends SlotFakeCraftingMatrix {

    public boolean visible = true;

    public RCSlotFakeCraftingMatrix(IItemHandler inv, int idx, int x, int y) {
        super(inv, idx, x, y);
    }

    public boolean shouldDisplay() {
        return visible;
    }
}
