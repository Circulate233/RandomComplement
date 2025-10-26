package com.circulation.random_complement.mixin.ae2fc.gui;

import appeng.container.slot.SlotFake;
import com.circulation.random_complement.common.util.MEHandler;
import com.circulation.random_complement.mixin.ae2.gui.MixinGuiMEMonitorable;
import com.glodblock.github.client.GuiExtendedFluidPatternTerminal;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiExtendedFluidPatternTerminal.class)
public abstract class MixinGuiExtendedFluidPatternTerminal extends MixinGuiMEMonitorable {

    public MixinGuiExtendedFluidPatternTerminal(Container container) {
        super(container);
    }

    @Inject(method = "drawSlot", at = @At(value = "HEAD"))
    private void drawSlotFake(Slot slot, CallbackInfo ci) {
        if (slot.xPos < 0 || slot.yPos < 0) return;
        if (slot instanceof SlotFake slotFake) {
            var item = slotFake.getDisplayStack();
            if (!item.isEmpty()) {
                if (r$getCraftablesCache().contains(MEHandler.packItem(item))) {
                    r$getPlusSlot().add(slotFake);
                }
            }
        }
    }
}
