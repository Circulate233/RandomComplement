package com.circulation.random_complement.mixin.ae2fc.news;

import appeng.container.slot.SlotFake;
import com.circulation.random_complement.common.util.MEHandler;
import com.circulation.random_complement.mixin.ae2.gui.MixinGuiMEMonitorable;
import com.glodblock.github.client.client.gui.GuiExtendedFluidPatternTerminal;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GuiExtendedFluidPatternTerminal.class)
public abstract class MixinGuiExtendedFluidPatternTerminal extends MixinGuiMEMonitorable {

    public MixinGuiExtendedFluidPatternTerminal(Container container) {
        super(container);
    }

    @Intrinsic
    public void drawSlot(@NotNull Slot slot) {
        if (slot.xPos < 0 || slot.yPos < 0) return;
        if (slot instanceof SlotFake slotFake) {
            if (!slotFake.shouldDisplay()) return;
            if (!slotFake.getDisplayStack().isEmpty()) {
                var item = slotFake.getDisplayStack();
                if (r$getCraftablesCache().contains(MEHandler.packAEItem(item))) {
                    r$getPlusSlot().add(slotFake);
                }
            }
        }
        super.drawSlot(slot);
    }

}