package com.circulation.random_complement.mixin.jei;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mezz.jei.api.gui.IGhostIngredientHandler;
import mezz.jei.bookmarks.BookmarkItem;
import mezz.jei.gui.ghost.GhostIngredientDrag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GhostIngredientDrag.class,remap = false)
public class MixinGhostIngredientDrag<T> {

    @Shadow
    @Final
    private T ingredient;

    @WrapOperation(method = "onClick", at = @At(value = "INVOKE", target = "Lmezz/jei/api/gui/IGhostIngredientHandler$Target;accept(Ljava/lang/Object;)V"))
    public void onClick(IGhostIngredientHandler.Target<?> instance, T i, Operation<Void> original) {
        if (this.ingredient instanceof BookmarkItem<?> b) {
            original.call(instance, b.ingredient);
            return;
        }
        original.call(instance, this.ingredient);
    }
}