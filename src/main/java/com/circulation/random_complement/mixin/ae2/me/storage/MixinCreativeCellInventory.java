package com.circulation.random_complement.mixin.ae2.me.storage;

import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.me.storage.CreativeCellInventory;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CreativeCellInventory.class)
public abstract class MixinCreativeCellInventory implements IMEInventoryHandler<IAEItemStack> {

    /**
     * @author sddsd2332
     * @reason 修改创造型ME存储元件存储上限
     *
     * <a href="https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/pull/708">代码来自GTNH团队的AE2U。</a>
     */
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lappeng/api/storage/data/IAEItemStack;setStackSize(J)Lappeng/api/storage/data/IAEStack;"), remap = false)
    public IAEStack<IAEItemStack> setStackSize(IAEItemStack instance, long l, Operation<IAEStack<IAEItemStack>> original) {
        return original.call(instance, 1L << 53 - 1);
    }

}