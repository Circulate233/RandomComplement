package com.circulation.random_complement.mixin.ae2.jei;

import appeng.client.gui.AEBaseGui;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mezz.jei.api.IBookmarkOverlay;
import mezz.jei.bookmarks.BookmarkItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AEBaseGui.class,remap = false)
public class JEIPluginMixin {

    @WrapOperation(method = "bookmarkedJEIghostItem",at = @At(value = "INVOKE", target = "Lmezz/jei/api/IBookmarkOverlay;getIngredientUnderMouse()Ljava/lang/Object;"))
    Object bookmarkedJEIghostItem(IBookmarkOverlay instance, Operation<Object> original) {
        var i = original.call(instance);
        if (i instanceof BookmarkItem<?> bItem){
            return bItem.ingredient;
        }
        if (i instanceof ItemStack || i instanceof FluidStack){
            return i;
        }
        return null;
    }
}
