package com.circulation.random_complement.common.integration.ae2.core;

import appeng.core.AELog;
import net.minecraft.client.resources.I18n;

public enum GuiColors {
    ProcessBarStartColor(0XFFE60A00),
    ProcessBarMiddleColor(0XFFE6E600),
    ProcessBarEndColor(0XFF0AE600);

    private final String u;
    private final int color;

    GuiColors(final int hex) {
        this.color = hex;
        this.u = "gui.color.appliedenergistics2." + this.name();
    }

    public int getColor() {
        String hex = I18n.format(this.u);
        int color = this.color;
        if (I18n.format(this.u).length() <= 8) {
            try {
                color = Integer.parseUnsignedInt(hex, 16);
            } catch (final NumberFormatException e) {
                AELog.warn("Couldn't format color correctly for: gui.color.appliedenergistics2 -> " + hex);
            }
        }
        return color;
    }

}
