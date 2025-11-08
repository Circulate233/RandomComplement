package com.circulation.random_complement.mixin.ae2.jei;

import appeng.client.gui.AEBaseGui;
import com.circulation.random_complement.RandomComplement;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = AEBaseGui.class)
public abstract class MixinAEBaseGui extends GuiContainer {

    public MixinAEBaseGui(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Intrinsic
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        if (!RandomComplement.proxy.isMouseHasItem()
            && this.hoveredSlot != null
            && this.hoveredSlot.getHasStack()
        ) {
            this.renderToolTip(this.hoveredSlot.getStack(), mouseX, mouseY);
        }
    }

}