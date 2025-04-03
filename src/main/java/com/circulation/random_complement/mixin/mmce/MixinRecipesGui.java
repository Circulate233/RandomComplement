package com.circulation.random_complement.mixin.mmce;

import hellfirepvp.modularmachinery.common.integration.preview.StructurePreviewWrapper;
import hellfirepvp.modularmachinery.common.item.ItemBlueprint;
import hellfirepvp.modularmachinery.common.lib.ItemsMM;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.Focus;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = RecipesGui.class,remap = false)
public class MixinRecipesGui {

    @Final
    @Shadow
    private List<RecipeLayout> recipeLayouts;
    @Final
    @Shadow
    private IRecipeGuiLogic logic;

    @Redirect(method = "actionPerformed",at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"),remap = true)
    protected Object actionPerformedMixin(List<RecipeLayout> instance, int i) {
        boolean isPreview = false;
        for (RecipeLayout recipeLayout : recipeLayouts) {
            if (((AccessorRecipeLayout)recipeLayout).getRecipeWrapper() instanceof StructurePreviewWrapper s){
                return randomComplement$getRecipe(s);
            }
        }
        return instance.get(i);
    }

    @Unique
    private RecipeLayout randomComplement$getRecipe(StructurePreviewWrapper preview){
        ItemStack bOut = new ItemStack(ItemsMM.blueprint);
        ItemBlueprint.setAssociatedMachine(bOut,((AccessorStructurePreviewWrapper)preview).getMachine());
        return RecipeLayout.create(0,this.logic.getSelectedRecipeCategory(),preview, new Focus<>(IFocus.Mode.OUTPUT, bOut),0,0);
    }

    @Mixin(value = StructurePreviewWrapper.class,remap = false)
    public interface AccessorStructurePreviewWrapper {

        @Accessor
        DynamicMachine getMachine();
    }

    @Mixin(value = RecipeLayout.class,remap = false)
    public interface AccessorRecipeLayout {

        @Accessor
        IRecipeWrapper getRecipeWrapper();
    }
}
