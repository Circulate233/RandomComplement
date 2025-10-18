package com.circulation.random_complement.mixin.ae2.new_patten_gui;

import appeng.parts.reporting.PartPatternTerminal;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PartPatternTerminal.class, remap = false)
public class MixinPartPatternTerminal {

    @WrapOperation(method = "<init>", at = @At(value = "NEW", target = "(Lappeng/util/inv/IAEAppEngInventory;I)Lappeng/tile/inventory/AppEngInternalInventory;", ordinal = 0))
    public AppEngInternalInventory newCrafting(IAEAppEngInventory inventory, int size, Operation<AppEngInternalInventory> original) {
        return original.call(inventory, 81);
    }

    @WrapOperation(method = "<init>", at = @At(value = "NEW", target = "(Lappeng/util/inv/IAEAppEngInventory;I)Lappeng/tile/inventory/AppEngInternalInventory;", ordinal = 1))
    public AppEngInternalInventory newOutput(IAEAppEngInventory inventory, int size, Operation<AppEngInternalInventory> original) {
        return original.call(inventory, 27);
    }
}