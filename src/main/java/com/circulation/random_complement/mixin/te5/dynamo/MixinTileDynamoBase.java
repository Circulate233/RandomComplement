package com.circulation.random_complement.mixin.te5.dynamo;

import cofh.api.core.IAccelerable;
import cofh.api.tileentity.IEnergyInfo;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.ISteamInfo;
import cofh.core.block.TileInventory;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import com.circulation.random_complement.RCConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.util.ITickable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TileDynamoBase.class, remap = false)
public abstract class MixinTileDynamoBase extends TileInventory implements ITickable, IAccelerable, IEnergyProvider, IReconfigurableFacing, ISidedInventory, IEnergyInfo, ISteamInfo {


    @Shadow
    protected abstract int getBasePower(int level);

    @Redirect(method = "installAugmentToSlot", at = @At(value = "INVOKE", target = "Lcofh/thermalexpansion/block/dynamo/TileDynamoBase;getBasePower(I)I"))
    public int setBasePower(TileDynamoBase instance, int level, @Local(ordinal = 0, argsOnly = true) int slot) {
        return getBasePower(level) * this.augments[slot].getCount();
    }

    @ModifyConstant(method = "installAugmentToSlot", constant = @Constant(intValue = 15, ordinal = 0))
    private int setDynamoEfficiency(int constant, @Local(ordinal = 0, argsOnly = true) int slot) {
        return RCConfig.TE5.FuelCatalyzerQuantityChanges ? RCConfig.TE5.FuelCatalyzer * this.augments[slot].getCount() : RCConfig.TE5.FuelCatalyzer;
    }

}
