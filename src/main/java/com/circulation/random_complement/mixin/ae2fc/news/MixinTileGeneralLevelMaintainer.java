package com.circulation.random_complement.mixin.ae2fc.news;

import com.circulation.random_complement.common.interfaces.RCAutoCraftingSource;
import com.glodblock.github.common.tile.TileGeneralLevelMaintainer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = TileGeneralLevelMaintainer.class, remap = false)
public class MixinTileGeneralLevelMaintainer implements RCAutoCraftingSource {
}
