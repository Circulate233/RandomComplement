package com.circulation.random_complement.common.handler;

import appeng.util.inv.WrapperChainedItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class RCWrapperChainedItemHandler extends WrapperChainedItemHandler {
    private IItemHandler[] itemHandler;
    private int[] baseIndex;
    private int slotCount;

    public RCWrapperChainedItemHandler(IItemHandler... itemHandler) {
        super(itemHandler);
        this.setItemHandlers(itemHandler);
    }

    private void setItemHandlers(IItemHandler[] handlers) {
        this.itemHandler = handlers;
        this.baseIndex = new int[this.itemHandler.length];
        int index = 0;

        for(int i = 0; i < this.itemHandler.length; ++i) {
            index += this.itemHandler[i].getSlots();
            this.baseIndex[i] = index;
        }

        this.slotCount = index;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }
}
