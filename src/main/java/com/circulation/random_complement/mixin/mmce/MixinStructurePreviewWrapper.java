package com.circulation.random_complement.mixin.mmce;

import github.kasuminova.mmce.client.gui.integration.GuiBlueprintScreenJEI;
import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.impl.preview.MachineStructurePreviewPanel;
import github.kasuminova.mmce.client.gui.widget.impl.preview.WorldSceneRendererWidget;
import github.kasuminova.mmce.client.preivew.PreviewPanels;
import hellfirepvp.modularmachinery.common.integration.preview.StructurePreviewWrapper;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.TaggedPositionBlockArray;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = StructurePreviewWrapper.class,remap = false)
public class MixinStructurePreviewWrapper {

    @Final
    @Shadow
    private DynamicMachine machine;
    @Shadow
    private GuiBlueprintScreenJEI gui;
    @Unique
    private static Field randomComplement$MachineStructurePreviewPanelField;

    static {
        try {
            randomComplement$MachineStructurePreviewPanelField = MachineStructurePreviewPanel.class.getDeclaredField("renderer");
            randomComplement$MachineStructurePreviewPanelField.setAccessible(true);
        } catch (NoSuchFieldException ignored) {

        }
    }

    @Redirect(method = "getIngredients",at = @At(value = "INVOKE", target = "Lhellfirepvp/modularmachinery/common/machine/TaggedPositionBlockArray;getIngredientList()Ljava/util/List;"))
    public List<List<ItemStack>> getIngredientsMixin(TaggedPositionBlockArray instance){
        if (gui != null) {
            try {
                WidgetController controller = this.gui.getWidgetController();
                var panel = (WorldSceneRendererWidget)randomComplement$MachineStructurePreviewPanelField.get(PreviewPanels.getPanel(this.machine, controller.getGui()));
                var list = panel.getPattern().getDescriptiveStackList(panel.getTickSnap(), panel.getWorldRenderer().getWorld(), panel.getRenderOffset());
                list.remove(0);
                List<List<ItemStack>> finalList = new ArrayList<>();
                list.forEach(itemStack -> finalList.add(Collections.singletonList(itemStack)));
                return finalList;
            } catch (IllegalAccessException e) {
                return instance.getIngredientList();
            }
        }
        return instance.getIngredientList();
    }

}
