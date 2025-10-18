package com.circulation.random_complement.mixin.ae2fc.gui;

import com.circulation.random_complement.mixin.ae2.gui.MixinGuiCraftConfirm;
import com.glodblock.github.client.GuiFCCraftConfirm;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiFCCraftConfirm.class, priority = 999)
public abstract class MixinGuiFCCraftConfirm extends MixinGuiCraftConfirm {

    @Shadow(remap = false)
    private GuiButton cancel;

    public MixinGuiFCCraftConfirm(Container container) {
        super(container);
    }

    @Override
    protected GuiButton r$getCancel() {
        return this.cancel;
    }

    @Intrinsic
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(offsetX, offsetY, mouseX, mouseY);
        if (r$getCancel().isMouseOver() && Loader.isModLoaded("jei")) {
            this.drawHoveringText(I18n.format("text.rc.confirm_cancel"), mouseX - offsetX, mouseY - offsetY);
        }
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    public void onActionPerformed1(GuiButton btn, CallbackInfo ci) {
        if (Loader.isModLoaded("jei") && btn == r$getCancel() && isShiftKeyDown()) rc$addMissBookmark();
    }
}