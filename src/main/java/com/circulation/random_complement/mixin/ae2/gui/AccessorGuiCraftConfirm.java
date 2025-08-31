package com.circulation.random_complement.mixin.ae2.gui;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.implementations.GuiCraftConfirm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiCraftConfirm.class,remap = false)
public interface AccessorGuiCraftConfirm {

    @Accessor
    IItemList<IAEItemStack> getMissing();
}
