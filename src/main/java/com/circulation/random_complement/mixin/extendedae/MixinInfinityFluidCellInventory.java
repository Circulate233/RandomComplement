package com.circulation.random_complement.mixin.extendedae;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEStack;
import com.github.glodblock.eae.common.inventory.InfinityFluidCellInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = InfinityFluidCellInventory.class, remap = false)
public class MixinInfinityFluidCellInventory {

    /**
     * @author Circulation_
     * @reason 修改创造型ME存储元件存储上限
     *
     * <a href="https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/pull/708">代码来自GTNH团队的AE2U。</a>
     */
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lappeng/api/storage/data/IAEFluidStack;setStackSize(J)Lappeng/api/storage/data/IAEStack;"), remap = false)
    public IAEStack<IAEFluidStack> setStackSize(IAEFluidStack instance, long l) {
        return instance.setStackSize(1L << 52);
    }

}
