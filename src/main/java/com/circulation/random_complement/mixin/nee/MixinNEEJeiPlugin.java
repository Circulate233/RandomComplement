package com.circulation.random_complement.mixin.nee;

import com.github.vfyjxf.nee.jei.NEEJeiPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = NEEJeiPlugin.class,remap = false)
public class MixinNEEJeiPlugin {

    @Redirect(method = "register",at = @At(value = "INVOKE", target = "Lmezz/jei/api/IModRegistry;addGhostIngredientHandler(Ljava/lang/Class;Lmezz/jei/api/gui/IGhostIngredientHandler;)V"))
    public void cancelNEEGhostIngredient(IModRegistry instance, Class<Object> tClass, IGhostIngredientHandler<GuiScreen> tiGhostIngredientHandler) {

    }
}
