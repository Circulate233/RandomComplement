package com.circulation.random_complement.mixin.jei;

import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.InputHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = InputHandler.class, remap = false)
public interface AccessorInputHandler {

    @Invoker("getIngredientUnderMouseForKey")
    IClickedIngredient<?> invokeGetIngredientUnderMouseForKey(int mouseX, int mouseY);
}
