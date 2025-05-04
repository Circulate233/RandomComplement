package com.circulation.random_complement.mixin.cofhcore;

import cofh.api.core.IAugmentable;
import cofh.api.item.IAugmentItem;
import cofh.api.item.IAugmentItem.AugmentType;
import cofh.core.gui.slot.SlotAugment;
import com.circulation.random_complement.RCConfig;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SlotAugment.class)
public abstract class MixinSlotAugment extends Slot {

    @Shadow(remap = false)
    IAugmentable myTile;

    public MixinSlotAugment(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    //修改插槽上限
    @Override
    public int getSlotStackLimit() {
        return RCConfig.COFHCORE.SocketLimitModified;
    }

    //TODO:如果物品是专业化升级，则设置插槽为1
    @Override
    public int getItemStackLimit(ItemStack stack) {
        return stack.getItem() instanceof IAugmentItem item && (item.getAugmentType(stack).equals(AugmentType.ADVANCED) || item.getAugmentType(stack).equals(AugmentType.MODE)) ? 1 : super.getItemStackLimit(stack);
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        if (myTile.getAugmentSlots()[getSlotIndex()] == ItemStack.EMPTY) {
            return ItemStack.EMPTY;
        }
        return myTile.getAugmentSlots()[getSlotIndex()].splitStack(amount);
    }
}
