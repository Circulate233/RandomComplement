package com.circulation.random_complement.mixin.ae2fc.gui;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiCraftConfirm;
import com.circulation.random_complement.client.RCGuiCraftConfirm;
import com.glodblock.github.client.GuiFCCraftConfirm;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiFCCraftConfirm.class)
public abstract class MixinGuiFCCraftConfirm extends GuiCraftConfirm implements RCGuiCraftConfirm {

    @Shadow(remap = false)
    private GuiButton cancel;

    public MixinGuiFCCraftConfirm(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(offsetX, offsetY, mouseX, mouseY);
        if (this.cancel.isMouseOver() && Loader.isModLoaded("jei")) {
            this.drawHoveringText(I18n.format("text.rc.confirm_cancel"), mouseX - offsetX, mouseY - offsetY);
        }
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    public void onActionPerformed1(GuiButton btn, CallbackInfo ci) {
        if (Loader.isModLoaded("jei") && btn == this.cancel && isShiftKeyDown()) rc$addMissBookmark();
    }
}