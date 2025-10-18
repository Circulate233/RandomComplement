package com.circulation.random_complement.mixin.ae2fc.tile;

import appeng.helpers.DualityInterface;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import com.glodblock.github.common.tile.TileDualInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TileDualInterface.class, remap = false)
public abstract class MixinTileDualInterface implements RCIConfigurableObject {
    @Shadow
    public abstract DualityInterface getInterfaceDuality();

    @Override
    public RCIConfigManager r$getConfigManager() {
        return ((RCIConfigurableObject) this.getInterfaceDuality()).r$getConfigManager();
    }
}