package com.circulation.random_complement.client.buttonsetting;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum InscriberMaxStackLimit implements InterfaceButton {
    SMALL,
    MEDIUM,
    BIG;

    private final String key = isClient() ? "gui.inscriber.max_stack_limit." + this.name() + ".text" : null;

    @Override
    @SideOnly(Side.CLIENT)
    public String getName(){
        return I18n.format("gui.inscriber.max_stack_limit.name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTooltip(){
        return I18n.format(key);
    }
}
