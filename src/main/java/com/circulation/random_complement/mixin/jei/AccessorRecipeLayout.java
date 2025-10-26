package com.circulation.random_complement.mixin.jei;

import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.gui.ingredients.GuiIngredientGroup;
import mezz.jei.gui.recipes.RecipeLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = RecipeLayout.class, remap = false)
public interface AccessorRecipeLayout {

    @Accessor("guiIngredientGroups")
    Map<IIngredientType<?>, GuiIngredientGroup<?>> getGuiIngredientGroups();
}
