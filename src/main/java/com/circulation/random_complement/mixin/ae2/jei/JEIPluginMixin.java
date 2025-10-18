package com.circulation.random_complement.mixin.ae2.jei;

import appeng.client.gui.AEBaseGui;
import com.circulation.random_complement.client.handler.RCJEIInputHandler;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AEBaseGui.class, remap = false)
public class JEIPluginMixin {

    /**
     * @author circulation
     * @reason 删除此部分，使用JEI自身实现进行处理
     */
    @Overwrite
    void bookmarkedJEIghostItem(int mouseX, int mouseY) {

    }

    @Inject(method = "drawScreen", at = @At("HEAD"), remap = true)
    public void onDraw(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (RCJEIInputHandler.getXy() != null && RCJEIInputHandler.isClick() && !RCJEIInputHandler.getXy().equals(Mouse.getX(), Mouse.getY())) {
            RCJEIInputHandler.setClick(false);
            RCJEIInputHandler.runClickCache();
        }
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    public void onGuiClosed(CallbackInfo ci) {
        RCJEIInputHandler.clearCache();
    }
}