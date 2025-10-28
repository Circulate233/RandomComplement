package com.circulation.random_complement.mixin.ae2.new_patten_gui;

import appeng.parts.reporting.PartPatternTerminal;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import com.circulation.random_complement.common.util.AppEngInternalFixedCapacityInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PartPatternTerminal.class, remap = false)
public class MixinPartPatternTerminal {

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(Lappeng/util/inv/IAEAppEngInventory;I)Lappeng/tile/inventory/AppEngInternalInventory;", ordinal = 0))
    public AppEngInternalInventory newCrafting(IAEAppEngInventory inventory, int size) {
        return new AppEngInternalFixedCapacityInventory(inventory, 81);
    }

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(Lappeng/util/inv/IAEAppEngInventory;I)Lappeng/tile/inventory/AppEngInternalInventory;", ordinal = 1))
    public AppEngInternalInventory newOutput(IAEAppEngInventory inventory, int size) {
        return new AppEngInternalFixedCapacityInventory(inventory, 27);
    }
}