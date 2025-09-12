package com.circulation.random_complement.client.buttonsetting;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum InscriberBlockMode implements InterfaceButton {
    OPEN,
    CLOSE;

    @SideOnly(Side.CLIENT)
    private final String key = "gui.inscriber.block_mode." + this.name() + ".text";

    @Override
    public String getName(){
        return I18n.format("gui.inscriber.block_mode.name");
    }

    @Override
    public String getTooltip(){
        return I18n.format(key);
    }
}
