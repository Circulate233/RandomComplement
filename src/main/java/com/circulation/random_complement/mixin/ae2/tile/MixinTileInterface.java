package com.circulation.random_complement.mixin.ae2.tile;

import appeng.helpers.DualityInterface;
import appeng.tile.misc.TileInterface;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TileInterface.class,remap = false)
public class MixinTileInterface implements RCIConfigurableObject {
    @Shadow
    @Final
    private DualityInterface duality;

    @Override
    public RCIConfigManager r$getConfigManager() {
        return ((RCIConfigurableObject) this.duality).r$getConfigManager();
    }
}