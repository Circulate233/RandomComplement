package com.circulation.random_complement.mixin.ae2;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.crafting.MECraftingInventory;
import appeng.util.item.RCCraftingInventorySnapshot;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.Iterator;

@Mixin(value = MECraftingInventory.class, remap = false)
public class MixinMECraftingInventory {

    @Shadow
    @Final
    private IItemList<IAEItemStack> localCache;

    @WrapOperation(
        method = "<init>(Lappeng/api/storage/data/IItemList;)V",
        at = @At(value = "INVOKE", target = "Lappeng/api/storage/data/IItemList;iterator()Ljava/util/Iterator;")
    )
    private Iterator<IAEItemStack> rc$importSnapshot(IItemList<IAEItemStack> source, Operation<Iterator<IAEItemStack>> original) {
        if (RCCraftingInventorySnapshot.tryCopyInto(source, localCache)) return Collections.emptyIterator();
        return original.call(source);
    }
}
