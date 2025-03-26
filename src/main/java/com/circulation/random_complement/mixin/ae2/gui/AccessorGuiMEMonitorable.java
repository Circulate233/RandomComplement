package com.circulation.random_complement.mixin.ae2.gui;

import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.me.ItemRepo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiMEMonitorable.class,remap = false)
public interface AccessorGuiMEMonitorable {
    @Accessor
    ItemRepo getRepo();
}
