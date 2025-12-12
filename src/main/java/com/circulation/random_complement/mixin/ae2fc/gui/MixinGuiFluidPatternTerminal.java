package com.circulation.random_complement.mixin.ae2fc.gui;

import appeng.container.slot.SlotFake;
import com.circulation.random_complement.common.util.MEHandler;
import com.circulation.random_complement.mixin.ae2.gui.MixinGuiMEMonitorable;
import com.glodblock.github.client.GuiFluidPatternTerminal;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiFluidPatternTerminal.class)
public abstract class MixinGuiFluidPatternTerminal extends MixinGuiMEMonitorable {

    public MixinGuiFluidPatternTerminal(Container container) {
        super(container);
    }

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Slot;getStack()Lnet/minecraft/item/ItemStack;", ordinal = 0))
    private void drawSlotFake(Slot slot, CallbackInfo ci) {
        if (slot.xPos < 0 || slot.yPos < 0) return;
        if (slot instanceof SlotFake slotFake) {
            if (!slotFake.shouldDisplay()) return;
            if (!slotFake.getDisplayStack().isEmpty()) {
                var item = slotFake.getDisplayStack();
                if (r$getCraftablesCache().contains(MEHandler.packAEItem(item))) {
                    r$getPlusSlot().add(slot);
                }
            }
        }
    }

}
