package com.circulation.random_complement.mixin.ae2fc.container;

import appeng.container.implementations.ContainerWirelessPatternTerminal;
import appeng.helpers.WirelessTerminalGuiObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ContainerWirelessPatternTerminal.class, remap = false)
public interface AccessorContainerWirelessPatternTerminal {
    @Accessor
    WirelessTerminalGuiObject getWirelessTerminalGUIObject();
}
