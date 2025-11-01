package com.circulation.random_complement.mixin.botaniverse;

import com.aeternal.botaniverse.blocks.tiles.TileMorePool;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.IManaCollector;

@Mixin(value = TileMorePool.class, remap = false)
public abstract class MixinTileMorePool implements IManaCollector {

    @Shadow(remap = false)
    public int manaCap;

    @Intrinsic
    public void onClientDisplayTick() {

    }

    @Intrinsic
    public float getManaYieldMultiplier(IManaBurst var1) {
        return 1.0F;
    }

    @Intrinsic
    public int getMaxMana() {
        return this.manaCap;
    }
}