package com.circulation.random_complement.mixin.jei;

import mezz.jei.gui.ghost.GhostIngredientDragManager;
import mezz.jei.input.InputHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = InputHandler.class, remap = false)
public interface AccessorInputHandler {

    @Accessor
    GhostIngredientDragManager getGhostIngredientDragManager();
}
