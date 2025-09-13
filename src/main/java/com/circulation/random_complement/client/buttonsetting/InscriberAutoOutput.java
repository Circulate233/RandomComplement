package com.circulation.random_complement.client.buttonsetting;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum InscriberAutoOutput implements InterfaceButton {
    OPEN,
    CLOSE;

    private final String key = isClient() ? "gui.inscriber.auto_output." + this.name() + ".text" : null;

    @Override
    @SideOnly(Side.CLIENT)
    public String getName(){
        return I18n.format("gui.inscriber.auto_output.name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTooltip(){
        return I18n.format(key);
    }
}
