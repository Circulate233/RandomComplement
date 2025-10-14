package com.circulation.random_complement.mixin.jei;

import com.circulation.random_complement.client.handler.RCJEIInputHandler;
import com.llamalad7.mixinextras.sugar.Local;
import mezz.jei.config.Config;
import mezz.jei.gui.ghost.GhostIngredientDragManager;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.InputHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InputHandler.class,remap = false)
public abstract class MixinInputHandler {

    @Shadow
    protected abstract <V> boolean handleMouseClickedFocus(int mouseButton, IClickedIngredient<V> clicked);

    @Shadow
    @Final
    private GhostIngredientDragManager ghostIngredientDragManager;

    @Inject(method = "handleMouseClick", at = @At(value = "INVOKE", target = "Lmezz/jei/gui/ghost/GhostIngredientDragManager;handleMouseClicked(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/GuiScreen;Lmezz/jei/input/IClickedIngredient;Lmezz/jei/gui/ingredients/IIngredientListElement;II)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void handleMouseClick(GuiScreen guiScreen, int mouseButton, int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir, @Local IIngredientListElement<?> listElement, @Local IClickedIngredient<?> clicked) {
        if (listElement == null) return;
        if (RCJEIInputHandler.isJeiGui(Minecraft.getMinecraft().currentScreen)) {

            RCJEIInputHandler.setClickCache(() -> {
                if (RCJEIInputHandler.isClick() && !GuiScreen.isShiftKeyDown()) {
                    if (clicked != null && Config.mouseClickToSeeRecipe()) {
                        this.handleMouseClickedFocus(mouseButton, clicked);
                    }
                } else {
                    this.ghostIngredientDragManager.handleMouseClicked(guiScreen.mc, guiScreen, clicked, listElement, mouseX, mouseY);
                }
            });

            cir.setReturnValue(true);
        }
    }
}