package com.circulation.random_complement.mixin.mmce;

import github.kasuminova.mmce.client.gui.widget.impl.preview.MachineStructurePreviewPanel;
import github.kasuminova.mmce.client.gui.widget.impl.preview.WorldSceneRendererWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MachineStructurePreviewPanel.class,remap = false)
public interface AccessorMachineStructurePreviewPanel {

    @Accessor
    WorldSceneRendererWidget getRenderer();
}
