package com.circulation.random_complement.mixin.ae2.jei;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import mezz.jei.render.IngredientListBatchRenderer;
import mezz.jei.render.IngredientListSlot;
import mezz.jei.render.IngredientRenderer;
import mezz.jei.render.ItemStackFastRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = IngredientListBatchRenderer.class,remap = false)
public abstract class MixinIngredientListBatchRenderer {

    @Shadow
    @Final
    @Mutable
    protected List<List<IngredientListSlot>> slots;
    @Shadow
    @Final
    @Mutable
    protected List<ItemStackFastRenderer> renderItems2d;
    @Shadow
    @Final
    @Mutable
    protected List<ItemStackFastRenderer> renderItems3d;
    @Shadow
    @Final
    @Mutable
    protected List<IngredientRenderer<?>> renderOther;

    @Inject(method = "<init>(Z)V",at = @At("TAIL"))
    public void onInit(CallbackInfo ci){
        slots = ObjectLists.synchronize(new ObjectArrayList<>());
        renderItems2d = ObjectLists.synchronize(new ObjectArrayList<>());
        renderItems3d = ObjectLists.synchronize(new ObjectArrayList<>());
        renderOther = ObjectLists.synchronize(new ObjectArrayList<>());
    }
}
