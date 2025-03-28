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

    @Redirect(method = "getIngredients",at = @At(value = "INVOKE", target = "Lhellfirepvp/modularmachinery/common/machine/TaggedPositionBlockArray;getIngredientList()Ljava/util/List;"))
    public List<List<ItemStack>> getIngredientsMixin(TaggedPositionBlockArray instance){
        if (gui != null) {
            try {
                WidgetController controller = this.gui.getWidgetController();
                Field machine = MachineStructurePreviewPanel.class.getDeclaredField("renderer");
                machine.setAccessible(true);
                var panel = (WorldSceneRendererWidget) machine.get(PreviewPanels.getPanel(this.machine, controller.getGui()));
                var list = panel.getPattern().getDescriptiveStackList(panel.getTickSnap(), panel.getWorldRenderer().getWorld(), panel.getRenderOffset());
                list.remove(0);
                List<List<ItemStack>> finalList = new ArrayList<>();
                list.forEach(itemStack -> finalList.add(Collections.singletonList(itemStack)));
                return finalList;
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }

        }
        return instance.getIngredientList();
    }
}
