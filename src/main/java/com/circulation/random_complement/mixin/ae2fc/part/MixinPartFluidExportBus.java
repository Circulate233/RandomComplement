package com.circulation.random_complement.mixin.ae2fc.part;

import com.circulation.random_complement.common.interfaces.RCAutoCraftingSource;
import com.glodblock.github.common.part.PartFluidExportBus;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = PartFluidExportBus.class, remap = false)
public class MixinPartFluidExportBus implements RCAutoCraftingSource {
}
