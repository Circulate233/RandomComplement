package com.circulation.random_complement.mixin.mmce;

import hellfirepvp.modularmachinery.common.integration.preview.StructurePreviewWrapper;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = StructurePreviewWrapper.class,remap = false)
public interface AccessorStructurePreviewWrapper {

    @Accessor
    DynamicMachine getMachine();
}
