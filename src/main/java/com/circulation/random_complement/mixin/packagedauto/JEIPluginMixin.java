package com.circulation.random_complement.mixin.packagedauto;

import com.circulation.random_complement.client.handler.RCJEIInputHandler;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import thelm.packagedauto.client.gui.GuiContainerTileBase;
import thelm.packagedauto.client.gui.GuiEncoder;
import thelm.packagedauto.container.ContainerEncoder;

@Mixin(value = GuiEncoder.class,remap = false)
public abstract class JEIPluginMixin extends GuiContainerTileBase<ContainerEncoder> {

    public JEIPluginMixin(ContainerEncoder containerEncoder) {
        super(containerEncoder);
    }

    @Intrinsic
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (RCJEIInputHandler.getXy() != null && RCJEIInputHandler.isClick() && !RCJEIInputHandler.getXy().equals(Mouse.getX(), Mouse.getY())) {
            RCJEIInputHandler.setClick(false);
            RCJEIInputHandler.runClickCache();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Intrinsic
    public void onGuiClosed() {
        super.onGuiClosed();
        RCJEIInputHandler.clearCache();
    }
}