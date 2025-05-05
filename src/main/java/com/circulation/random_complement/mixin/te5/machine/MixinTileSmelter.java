package com.circulation.random_complement.mixin.te5.machine;

import cofh.thermalexpansion.block.machine.TileMachineBase;
import cofh.thermalexpansion.block.machine.TileSmelter;
import com.circulation.random_complement.RCConfig;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = TileSmelter.class, remap = false)
public abstract class MixinTileSmelter extends TileMachineBase {


    @ModifyConstant(method = "installAugmentToSlot", constant = @Constant(intValue = 15, ordinal = 0))
    private int setMachineSmelterReuseChance(int constant, @Local(ordinal = 0) int slot) {
        return constant * this.augments[slot].getCount();
    }

    @ModifyConstant(method = "installAugmentToSlot", constant = @Constant(intValue = 15, ordinal = 1))
    private int setMachineSmelterEnergyMod(int constant, @Local(ordinal = 0) int slot) {
        return RCConfig.TE5.IncreasedEnergyConsumption ? constant * this.augments[slot].getCount() : constant;
    }
}
