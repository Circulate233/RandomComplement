package com.circulation.random_complement.client.buttonsetting;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum Action implements InterfaceButton  {
    MULTIPLY_2,
    MULTIPLY_3,
    DIVIDE_2,
    DIVIDE_3;

    @SideOnly(Side.CLIENT)
    private final String nameKey = "gui.action." + this.name() + ".name";
    @SideOnly(Side.CLIENT)
    private final String key = "gui.pattern_term.auto_fill_pattern." + this.name() + ".text";

    @Override
    public String getName(){
        return I18n.format(nameKey);
    }

    @Override
    public String getTooltip(){
        return I18n.format(key);
    }
}
