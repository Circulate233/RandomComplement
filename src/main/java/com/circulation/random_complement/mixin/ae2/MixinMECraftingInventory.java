package com.circulation.random_complement.mixin.ae2;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.crafting.MECraftingInventory;
import com.circulation.random_complement.common.interfaces.RCItemList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;

@Mixin(value = MECraftingInventory.class, remap = false)
public class MixinMECraftingInventory {

    @Shadow
    @Final
    private IItemList<IAEItemStack> localCache;

    @Unique
    private static void rc$reserve(IItemList<IAEItemStack> list, IItemList<IAEItemStack> source) {
        if (list instanceof RCItemList rc && source != null) {
            rc.rc$reserveCapacity(source.size());
        }
    }

    @Redirect(
        method = "<init>(Lappeng/api/storage/data/IItemList;)V",
        at = @At(value = "INVOKE", target = "Lappeng/api/storage/data/IItemList;iterator()Ljava/util/Iterator;")
    )
    private Iterator<IAEItemStack> rc$reserveForCopy(IItemList<IAEItemStack> instance) {
        if (instance == null) {
            throw new NullPointerException("itemList");
        }
        rc$reserve(this.localCache, instance);
        return instance.iterator();
    }
}
