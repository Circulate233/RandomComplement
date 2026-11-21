package com.circulation.random_complement.mixin.ae2;

import appeng.api.storage.data.IItemList;
import appeng.util.inv.ItemListIgnoreCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemListIgnoreCrafting.class, remap = false)
public interface AccessorItemListIgnoreCrafting {
    @Accessor("target")
    IItemList<?> rc$getTarget();
}
