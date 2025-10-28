package com.circulation.random_complement.mixin.ae2.new_patten_gui;

import appeng.container.implementations.ContainerWirelessCraftingTerminal;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import com.circulation.random_complement.common.util.AppEngInternalFixedCapacityInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ContainerWirelessCraftingTerminal.class, remap = false)
public abstract class MixinContainerWirelessCraftingTerminal implements IAEAppEngInventory {

    @Redirect(method = "loadFromNBT", at = @At(value = "NEW", target = "(Lappeng/util/inv/IAEAppEngInventory;I)Lappeng/tile/inventory/AppEngInternalInventory;"))
    protected AppEngInternalInventory loadFromNBT(IAEAppEngInventory inventory, int size) {
        return new AppEngInternalFixedCapacityInventory(this, 9);
    }
}
