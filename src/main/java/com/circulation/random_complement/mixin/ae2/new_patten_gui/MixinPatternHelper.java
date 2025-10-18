package com.circulation.random_complement.mixin.ae2.new_patten_gui;

import appeng.helpers.PatternHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PatternHelper.class, remap = false)
public class MixinPatternHelper {

    @WrapOperation(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/inventory/Container;II)Lnet/minecraft/inventory/InventoryCrafting;"))
    public InventoryCrafting onInit(Container eventHandlerIn, int width, int height, Operation<InventoryCrafting> original) {
        return original.call(eventHandlerIn, 10, 10);
    }
}