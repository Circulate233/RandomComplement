package com.circulation.random_complement.mixin.ae2.part;

import appeng.parts.automation.PartExportBus;
import com.circulation.random_complement.common.interfaces.RCAutoCraftingSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = PartExportBus.class, remap = false)
public class MixinPartExportBus implements RCAutoCraftingSource {
}
