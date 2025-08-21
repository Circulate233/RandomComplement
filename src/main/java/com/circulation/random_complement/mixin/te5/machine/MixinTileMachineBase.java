package com.circulation.random_complement.mixin.te5.machine;

import cofh.api.core.IAccelerable;
import cofh.core.block.TilePowered;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import com.circulation.random_complement.RCConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.ITickable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TileMachineBase.class, remap = false)
public abstract class MixinTileMachineBase extends TilePowered implements IAccelerable, ITickable {

    @Shadow
    protected abstract int getBasePower(int level);

    @ModifyConstant(method = "postAugmentInstall", constant = @Constant(intValue = 95))
    private int setReuseChance(int constant) {
        return RCConfig.TE5.ReuseItemChance ? 100 : constant;
    }


    @Redirect(method = "installAugmentToSlot", at = @At(value = "INVOKE", target = "Lcofh/thermalexpansion/block/machine/TileMachineBase;getBasePower(I)I"))
    public int setBasePower(TileMachineBase instance, int level, @Local(ordinal = 0, argsOnly = true) int slot) {
        return getBasePower(level) * this.augments[slot].getCount();
    }

    @ModifyConstant(method = "installAugmentToSlot", constant = @Constant(intValue = 10, ordinal = 0))
    private int setMachinePowerEnergyMod(int constant, @Local(ordinal = 0, argsOnly = true) int slot) {
        return RCConfig.TE5.IncreasedEnergyConsumption ? constant * this.augments[slot].getCount() : constant;
    }

    @ModifyConstant(method = "installAugmentToSlot", constant = @Constant(intValue = 15))
    private int setSecondaryChance(int constant, @Local(ordinal = 0, argsOnly = true) int slot) {
        return constant * this.augments[slot].getCount();
    }

    @ModifyConstant(method = "installAugmentToSlot", constant = @Constant(intValue = 10, ordinal = 1))
    private int setMachineSecondaryEnergyMod(int constant, @Local(ordinal = 0, argsOnly = true) int slot) {
        return RCConfig.TE5.IncreasedEnergyConsumption ? constant * this.augments[slot].getCount() : constant;
    }
}
