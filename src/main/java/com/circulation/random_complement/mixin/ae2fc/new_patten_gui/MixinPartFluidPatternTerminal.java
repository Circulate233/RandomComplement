package com.circulation.random_complement.mixin.ae2fc.new_patten_gui;

import appeng.tile.inventory.AppEngInternalInventory;
import com.glodblock.github.common.part.PartFluidPatternTerminal;
import com.glodblock.github.inventory.ExAppEngInternalInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PartFluidPatternTerminal.class)
public class MixinPartFluidPatternTerminal {

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(Lappeng/tile/inventory/AppEngInternalInventory;)Lcom/glodblock/github/inventory/ExAppEngInternalInventory;", remap = false))
    public ExAppEngInternalInventory newCrafting(AppEngInternalInventory inv) {
        return new ExAppEngInternalInventory(inv) {
            @Override
            public void setSize(int size) {
            }
        };
    }
}