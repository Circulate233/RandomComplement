package com.circulation.random_complement.mixin.jeiu;

import com.circulation.random_complement.mixin.jei.AccessorIngredientListBatchRenderer;
import com.github.vfyjxf.jeiutilities.gui.history.AdvancedIngredientGrid;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.gui.overlay.GridAlignment;
import mezz.jei.gui.overlay.IngredientGrid;
import mezz.jei.input.MouseHelper;
import mezz.jei.render.IngredientListBatchRenderer;
import mezz.jei.render.IngredientListSlot;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = AdvancedIngredientGrid.class, remap = false)
public class MixinAdvancedIngredientGrid extends IngredientGrid {

    @Shadow
    @Final
    private IngredientListBatchRenderer guiHistoryIngredientSlots;

    @Shadow
    private boolean showHistory;

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

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lmezz/jei/render/IngredientListBatchRenderer;getAllGuiIngredientSlots()Ljava/util/List;"))
    public List<IngredientListSlot> draw(IngredientListBatchRenderer instance) {
        return ((AccessorIngredientListBatchRenderer) instance).getSlots().get(0);
    }

    @Redirect(method = "draw", at = @At(value = "FIELD", target = "Lcom/github/vfyjxf/jeiutilities/gui/history/AdvancedIngredientGrid;showHistory:Z", ordinal = 0, opcode = Opcodes.GETFIELD))
    public boolean draw(AdvancedIngredientGrid instance) {
        return this.showHistory && guiHistoryIngredientSlots.size() > 0;
    }

}