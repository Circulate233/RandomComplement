package com.circulation.random_complement.mixin.jeiu;

import com.github.vfyjxf.jeiutilities.gui.history.AdvancedIngredientGrid;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.gui.overlay.GridAlignment;
import mezz.jei.gui.overlay.IngredientGrid;
import mezz.jei.input.MouseHelper;
import mezz.jei.render.IngredientListBatchRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AdvancedIngredientGrid.class, remap = false)
public class MixinAdvancedIngredientGrid extends IngredientGrid {

    @Shadow
    @Final
    private IngredientListBatchRenderer guiHistoryIngredientSlots;

    public MixinAdvancedIngredientGrid(IngredientListBatchRenderer guiIngredientSlots, GridAlignment alignment) {
        super(guiIngredientSlots, alignment);
    }

    @Override
    public IIngredientListElement<?> getElementUnderMouse() {
        IIngredientListElement<?> e = super.getElementUnderMouse();
        if (e != null) return e;
        var r = this.guiHistoryIngredientSlots.getHovered(MouseHelper.getX(), MouseHelper.getY());
        return r != null ? r.getElement() : null;
    }

}