package com.circulation.random_complement.mixin.ae2.new_patten_gui;

import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.container.slot.SlotFake;
import com.circulation.random_complement.client.RCGuiScrollbar;
import com.circulation.random_complement.client.RCScrollbarGui;
import com.circulation.random_complement.common.interfaces.RCPatternEncoder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiPatternTerm.class)
public abstract class MixinGuiPatternTerm extends MixinGuiMEMonitorableTwo implements RCScrollbarGui {

    @Unique
    private RCGuiScrollbar r$scrollbar;
    @Shadow(remap = false)
    @Final
    private ContainerPatternEncoder container;
    @Unique
    private int r$slotLeft = Integer.MAX_VALUE;
    @Unique
    private int r$slotTop = Integer.MAX_VALUE;

    public MixinGuiPatternTerm(Container container) {
        super(container);
    }

    @Unique
    protected ContainerPatternEncoder r$getContainer() {
        return this.container;
    }

    @Override
    protected void r$addScrollBars(){
        r$getScrollBars().add(r$scrollbar = new RCGuiScrollbar());
    }

    @Intrinsic
    public void onCurrentScrollChance(int currentScroll) {
        ((RCPatternEncoder) container).setRCSlot(currentScroll);
    }

    @Inject(method = "initGui", at = @At("TAIL"))
    public void onInitGui(CallbackInfo ci) {
        for (var inventorySlot : container.inventorySlots) {
            if (inventorySlot instanceof SlotFake slotFake) {
                if (slotFake.xPos < r$slotLeft && slotFake.xPos > 0) r$slotLeft = slotFake.xPos;
                if (slotFake.yPos < r$slotTop && slotFake.yPos > 0) r$slotTop = slotFake.yPos;
            }
        }
        r$scrollbar.setWidthEx(175);
        r$scrollbar.setTop(r$slotTop).setLeft(r$slotLeft - 14).setHeight(3 * 18 - 2).setWidth(12);
        r$scrollbar.setRange(0, 9, 1);
        r$scrollbar.setRcScrollbarGui(this);
        r$scrollbar.setDrawBG(() -> {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(r$slotLeft - 13 + guiLeft - 2, r$slotTop + guiTop - 1, 174, 17, 14, 3 * 18);
        });
        ((RCPatternEncoder) container).setRCSlot(r$scrollbar.getCurrentScroll());
    }

    @Override
    protected void r$setScrollBar() {
        r$scrollbar.setVisible(!r$getContainer().isCraftingMode());
    }

}