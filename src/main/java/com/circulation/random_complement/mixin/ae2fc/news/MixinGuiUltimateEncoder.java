package com.circulation.random_complement.mixin.ae2fc.news;

import appeng.container.slot.SlotFake;
import com.circulation.random_complement.client.RCAECraftablesGui;
import com.circulation.random_complement.common.util.MEHandler;
import com.circulation.random_complement.mixin.ae2.gui.MixinAEBaseGui;
import com.glodblock.github.client.GuiUltimateEncoder;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GuiUltimateEncoder.class, remap = false)
public abstract class MixinGuiUltimateEncoder extends MixinAEBaseGui implements RCAECraftablesGui {

    public MixinGuiUltimateEncoder(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Override
    public void drawSlot(@NotNull Slot slot) {
        if (!this.r$getCraftablesCache().isEmpty() && slot instanceof SlotFake slotFake) {
            if (!slotFake.shouldDisplay()) return;
            var item = slotFake.getDisplayStack();
            if (!item.isEmpty()) {
                if (this.r$getCraftablesCache().contains(MEHandler.packAEItem(item))) {
                    r$getPlusSlot().add(slotFake);
                }
            }
        }
        super.drawSlot(slot);
    }
}