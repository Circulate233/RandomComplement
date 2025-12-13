package com.circulation.random_complement.mixin.util;

import appeng.client.gui.AEBaseGui;

public class FCClassUtil {

    public static final Class<AEBaseGui> fluidPatternTerminal;
    public static final Class<AEBaseGui> wirelessFluidPatternTerminal;
    public static final Class<AEBaseGui> extendedFluidPatternTerminal;

    static {
        Class<AEBaseGui> fluidPatternTerminal1;
        Class<AEBaseGui> extendedFluidPatternTerminal1;
        Class<AEBaseGui> wirelessFluidPatternTerminal1;
        try {
            fluidPatternTerminal1 = (Class<AEBaseGui>) Class.forName("com.glodblock.github.client.GuiFluidPatternTerminal");
            extendedFluidPatternTerminal1 = (Class<AEBaseGui>) Class.forName("com.glodblock.github.client.GuiExtendedFluidPatternTerminal");
            wirelessFluidPatternTerminal1 = (Class<AEBaseGui>) Class.forName("com.glodblock.github.client.GuiWirelessFluidPatternTerminal");
        } catch (ClassNotFoundException ignored) {
            try {
                fluidPatternTerminal1 = (Class<AEBaseGui>) Class.forName("com.glodblock.github.client.client.gui.GuiFluidPatternTerminal");
                extendedFluidPatternTerminal1 = (Class<AEBaseGui>) Class.forName("com.glodblock.github.client.client.gui.GuiExtendedFluidPatternTerminal");
                wirelessFluidPatternTerminal1 = (Class<AEBaseGui>) Class.forName("com.glodblock.github.client.client.gui.GuiWirelessFluidPatternTerminal");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        fluidPatternTerminal = fluidPatternTerminal1;
        extendedFluidPatternTerminal = extendedFluidPatternTerminal1;
        wirelessFluidPatternTerminal = wirelessFluidPatternTerminal1;
    }
}