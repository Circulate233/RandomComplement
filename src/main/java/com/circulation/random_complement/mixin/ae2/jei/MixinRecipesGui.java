package com.circulation.random_complement.mixin.ae2.jei;

import com.circulation.random_complement.client.RCAECraftablesGui;
import com.circulation.random_complement.common.util.MEHandler;
import com.circulation.random_complement.common.util.XYPair;
import com.circulation.random_complement.mixin.jei.AccessorGuiIngredient;
import com.circulation.random_complement.mixin.jei.AccessorRecipeLayout;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = RecipesGui.class, remap = false)
public class MixinRecipesGui extends GuiScreen {

    @Unique
    private final List<XYPair> r$slots = new ObjectArrayList<>();
    @Shadow
    @Final
    private List<RecipeLayout> recipeLayouts;

    @Shadow
    @Nullable
    private GuiScreen parentScreen;

    @Unique
    private boolean r$isCraftablesGui;

    @Inject(method = "updateLayout", at = @At(value = "INVOKE", target = "Lmezz/jei/gui/recipes/RecipeCatalysts;updateLayout(Ljava/util/List;Lmezz/jei/gui/recipes/RecipesGui;)V", shift = At.Shift.AFTER))
    private void updateLayout(CallbackInfo ci) {
        if (this.parentScreen instanceof RCAECraftablesGui gui) {
            r$isCraftablesGui = true;
            r$slots.clear();
            for (RecipeLayout r : this.recipeLayouts) {
                final var l = (AccessorRecipeLayout) r;
                for (var group : l.getGuiIngredientGroups().values()) {
                    for (var ingredient : group.getGuiIngredients().values()) {
                        for (var o : ingredient.getAllIngredients()) {
                            if (gui.r$getCraftablesCache().contains(MEHandler.packAEItem(o))) {
                                final var i = (AccessorGuiIngredient) ingredient;
                                r$slots.add(
                                    XYPair.of(
                                        ingredient.getRect().x + r.getPosX() + i.getXPadding(),
                                        ingredient.getRect().y + r.getPosY() + i.getXPadding()
                                    )
                                );
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            r$isCraftablesGui = false;
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lmezz/jei/gui/recipes/RecipeGuiTabs;draw(Lnet/minecraft/client/Minecraft;II)V", shift = At.Shift.AFTER, remap = false), remap = true)
    public void drawSlot(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (r$isCraftablesGui) {
            MEHandler.drawXYPluses(r$slots);
        }
    }
}