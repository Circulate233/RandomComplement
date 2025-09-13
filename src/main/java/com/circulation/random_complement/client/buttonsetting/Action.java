package com.circulation.random_complement.client.buttonsetting;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum Action implements InterfaceButton {
    MULTIPLY_2,
    MULTIPLY_3,
    DIVIDE_2,
    DIVIDE_3;

    private final String nameKey = isClient() ? "gui.action." + this.name() + ".name" : null;
    private final String key = isClient() ? "gui.pattern_term.auto_fill_pattern." + this.name() + ".text" : null;

    @Override
    @SideOnly(Side.CLIENT)
    public String getName(){
        return I18n.format(nameKey);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTooltip(){
        return I18n.format(key);
    }
}
