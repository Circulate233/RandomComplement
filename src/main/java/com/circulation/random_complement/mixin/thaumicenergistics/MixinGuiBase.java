package com.circulation.random_complement.mixin.thaumicenergistics;

import com.circulation.random_complement.common.handler.MEHandler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumicenergistics.client.gui.GuiBase;

import java.util.List;

@Mixin(GuiBase.class)
public abstract class MixinGuiBase extends GuiContainer {
    public MixinGuiBase(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Unique
    private final List<Slot> r$plusSlot = new ObjectArrayList<>();

    @Unique
    protected List<Slot> r$getPlusSlot() {
        return r$plusSlot;
    }

    @Inject(method = "renderHoveredToolTip", at = @At("HEAD"))
    public void drawPlusSlot(int mouseX, int mouseY, CallbackInfo ci) {
        GlStateManager.translate(this.guiLeft, this.guiTop, 250);
        MEHandler.drawSlotPluses(r$getPlusSlot());
        GlStateManager.translate(-this.guiLeft, -this.guiTop, -250);
    }
}
