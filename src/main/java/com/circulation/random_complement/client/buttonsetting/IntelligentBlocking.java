package com.circulation.random_complement.client.buttonsetting;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum IntelligentBlocking implements InterfaceButton {
    OPEN,
    CLOSE;

    @SideOnly(Side.CLIENT)
    private final String key = "gui.intelligent_blocking." + this.name() + ".text";

    @Override
    public String getName(){
        return I18n.format("gui.intelligent_blocking.name");
    }

    @Override
    public String getTooltip(){
        return I18n.format(key);
    }
}
