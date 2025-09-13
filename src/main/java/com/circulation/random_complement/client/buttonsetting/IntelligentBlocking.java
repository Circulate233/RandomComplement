package com.circulation.random_complement.client.buttonsetting;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum IntelligentBlocking implements InterfaceButton {
    OPEN,
    CLOSE;

    private final String key = isClient() ? "gui.intelligent_blocking." + this.name() + ".text" : null;

    @Override
    @SideOnly(Side.CLIENT)
    public String getName(){
        return I18n.format("gui.intelligent_blocking.name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTooltip(){
        return I18n.format(key);
    }
}
