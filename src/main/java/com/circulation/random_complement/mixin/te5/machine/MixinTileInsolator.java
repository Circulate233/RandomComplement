package com.circulation.random_complement.mixin.te5.machine;

import cofh.thermalexpansion.block.machine.TileInsolator;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import com.circulation.random_complement.RCConfig;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = TileInsolator.class, remap = false)
public class MixinTileInsolator extends TileMachineBase {

    @ModifyConstant(method = "installAugmentToSlot", constant = @Constant(intValue = 20))
    private int setMachineInsolatorFertilizerReuseChance(int constant, @Local(ordinal = 0) int slot) {
        return constant * this.augments[slot].getCount();
    }

    @ModifyConstant(method = "installAugmentToSlot", constant = @Constant(intValue = 15))
    private int setMachineInsolatorFertilizerEnergyMod(int constant, @Local(ordinal = 0) int slot) {
        return RCConfig.TE5.IncreasedEnergyConsumption ? constant * this.augments[slot].getCount() : constant;
    }
}
