package com.circulation.random_complement.mixin.enderutilities.ftblib;

import fi.dy.masa.enderutilities.gui.client.base.GuiEnderUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiEnderUtilities.class, remap = false)
public class MixinGuiEnderUtilities {

    @Unique
    private static final Class<?> r$ClassSidebarGroup;

    static {
        try {
            r$ClassSidebarGroup = Class.forName("com.feed_the_beast.ftblib.client.FTBLibClientEventHandler$GuiButtonSidebarGroup");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiButton;mousePressed(Lnet/minecraft/client/Minecraft;II)Z", remap = true))
    protected boolean mouseClicked(GuiButton instance, Minecraft mc, int mouseX, int mouseY) {
        if (r$ClassSidebarGroup.isInstance(instance)) {
            return false;
        }
        return instance.mousePressed(mc, mouseX, mouseY);
    }

}