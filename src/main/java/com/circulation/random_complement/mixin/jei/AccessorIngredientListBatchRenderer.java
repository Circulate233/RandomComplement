package com.circulation.random_complement.mixin.jei;

import mezz.jei.render.IngredientListBatchRenderer;
import mezz.jei.render.IngredientListSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = IngredientListBatchRenderer.class, remap = false)
public interface AccessorIngredientListBatchRenderer {

    @Accessor("slots")
    List<List<IngredientListSlot>> getSlots();
}
