package com.circulation.random_complement.mixin.ae2.gui;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.implementations.GuiCraftingCPU;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = GuiCraftingCPU.class, remap = false)
public interface AccessorGuiCraftingCPU extends AccessorAEBaseGui {

    @Accessor
    int getTooltip();

    @Accessor
    List<IAEItemStack> getVisual();

}
