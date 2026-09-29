package com.circulation.random_complement.mixin.ae2fc.old;

import com.circulation.random_complement.common.interfaces.RCAutoCraftingSource;
import com.glodblock.github.common.tile.TileFluidLevelMaintainer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = TileFluidLevelMaintainer.class, remap = false)
public class MixinTileFluidLevelMaintainer implements RCAutoCraftingSource {
}
