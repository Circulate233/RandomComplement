package com.circulation.random_complement.client;

import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.SlotPatternOutputs;
import net.minecraftforge.items.IItemHandler;

public class RCSlotPatternOutputs extends SlotPatternOutputs {

    public boolean visible = true;

    public RCSlotPatternOutputs(IItemHandler inv, IOptionalSlotHost containerBus, int idx, int x, int y, int offX, int offY, int groupNum) {
        super(inv, containerBus, idx, x, y, offX, offY, groupNum);
    }

    public boolean isSlotEnabled() {
        return visible;
    }

    public boolean shouldDisplay() {
        if (super.isSlotEnabled()) {
            return visible;
        }
        return false;
    }
}
