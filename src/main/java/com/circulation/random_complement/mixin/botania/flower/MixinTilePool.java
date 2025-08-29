package com.circulation.random_complement.mixin.botania.flower;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.IManaCollector;
import vazkii.botania.common.block.tile.mana.TilePool;

@Mixin(TilePool.class)
public abstract class MixinTilePool implements IManaCollector {

    @Shadow(remap = false)
    public int manaCap;

    @Unique
    @Override
    public void onClientDisplayTick() {

    }

    @Unique
    @Override
    public float getManaYieldMultiplier(IManaBurst var1) {
        return 1.0F;
    }

    @Unique
    @Override
    public int getMaxMana() {
        return this.manaCap;
    }
}
