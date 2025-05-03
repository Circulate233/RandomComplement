package com.circulation.random_complement.mixin.te5;

import cofh.api.core.IAccelerable;
import cofh.core.block.TilePowered;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.ITickable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TileMachineBase.class, remap = false)
public abstract class MixinTileMachineBase extends TilePowered implements IAccelerable, ITickable {

    @Shadow
    protected abstract int getBasePower(int level);

    @Redirect(method = "installAugmentToSlot", at = @At(value = "INVOKE", target = "Lcofh/thermalexpansion/block/machine/TileMachineBase;getBasePower(I)I"))
    public int setBasePower(TileMachineBase instance, int level, @Local(ordinal = 0) int slot) {
        return getBasePower(level) * this.augments[slot].getCount();
    }

}
