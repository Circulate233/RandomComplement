package com.circulation.random_complement.mixin.ae2.gui;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiScrollbar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AEBaseGui.class, remap = false)
public interface AccessorAEBaseGui {

    @Invoker("getScrollBar")
    GuiScrollbar invokerGetScrollBar();
}
