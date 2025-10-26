package com.circulation.random_complement.mixin.jei;

import mezz.jei.gui.ingredients.GuiIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiIngredient.class, remap = false)
public interface AccessorGuiIngredient {

    @Accessor("xPadding")
    int getXPadding();

    @Accessor("yPadding")
    int getYPadding();
}