package com.circulation.random_complement.client.buttonsetting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface InterfaceButton {

    @SideOnly(Side.CLIENT)
    String getName();

    @SideOnly(Side.CLIENT)
    String getTooltip();

}
