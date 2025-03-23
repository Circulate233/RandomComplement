package com.circulation.random_complement.mixin.mmce;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.recipes.RecipeLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeLayout.class,remap = false)
public interface AccessorRecipeLayout {

    @Accessor
    IRecipeWrapper getRecipeWrapper();
}
