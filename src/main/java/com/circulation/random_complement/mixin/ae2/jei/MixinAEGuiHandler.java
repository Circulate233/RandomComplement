package com.circulation.random_complement.mixin.ae2.jei;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.AEGuiHandler;
import mezz.jei.api.gui.IGhostIngredientHandler;
import mezz.jei.bookmarks.BookmarkItem;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = AEGuiHandler.class,remap = false)
public abstract class MixinAEGuiHandler {

    @Shadow
    public abstract List<IGhostIngredientHandler.Target<?>> getTargets(GuiScreen par1, Object par2, boolean par3);

    @Inject(method = "getTargets(Lappeng/client/gui/AEBaseGui;Ljava/lang/Object;Z)Ljava/util/List;",at = @At("HEAD"), cancellable = true)
    public void getTargets(AEBaseGui gui, Object ingredient, boolean doStart, CallbackInfoReturnable<List<IGhostIngredientHandler.Target<?>>> cir) {
        if (ingredient instanceof BookmarkItem<?> i){
            cir.setReturnValue(this.getTargets(gui,i.ingredient,doStart));
        }
    }

    @Redirect(method = "getTargets(Lappeng/client/gui/AEBaseGui;Ljava/lang/Object;Z)Ljava/util/List;",at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;isButtonDown(I)Z"))
    public boolean getTargets(int button) {
        return !Mouse.isButtonDown(0);
    }
}
