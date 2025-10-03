package com.circulation.random_complement.mixin.ae2.gui;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiMEMonitorable;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GuiMEMonitorable.class,remap = false)
public abstract class MixinGuiMEMonitorableTwo extends AEBaseGui {
    public MixinGuiMEMonitorableTwo(Container container) {
        super(container);
    }

}
