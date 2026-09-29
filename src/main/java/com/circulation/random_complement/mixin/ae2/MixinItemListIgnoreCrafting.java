package com.circulation.random_complement.mixin.ae2;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.util.inv.ItemListIgnoreCrafting;
import com.circulation.random_complement.common.interfaces.RCItemList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ItemListIgnoreCrafting.class, remap = false)
public class MixinItemListIgnoreCrafting implements RCItemList {

    @Shadow
    @Final
    private IItemList<IAEItemStack> target;

    @Override
    public void rc$reserveCapacity(int expected) {
        if (target instanceof RCItemList rc) {
            rc.rc$reserveCapacity(expected);
        }
    }
}