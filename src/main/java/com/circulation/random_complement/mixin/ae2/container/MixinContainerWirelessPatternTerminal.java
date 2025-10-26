package com.circulation.random_complement.mixin.ae2.container;

import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.container.implementations.ContainerWirelessPatternTerminal;
import appeng.helpers.WirelessTerminalGuiObject;
import com.circulation.random_complement.common.util.MEHandler;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerWirelessPatternTerminal.class, remap = false)
public abstract class MixinContainerWirelessPatternTerminal extends ContainerPatternEncoder {

    protected MixinContainerWirelessPatternTerminal(InventoryPlayer ip, ITerminalHost monitorable, boolean bindInventory) {
        super(ip, monitorable, bindInventory);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(InventoryPlayer ip, WirelessTerminalGuiObject gui, CallbackInfo ci) {
        MEHandler.refillBlankPatterns(this, patternSlotIN);
    }
}
