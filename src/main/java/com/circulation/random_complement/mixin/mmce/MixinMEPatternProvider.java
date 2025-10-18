package com.circulation.random_complement.mixin.mmce;

import appeng.helpers.DualityInterface;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import github.kasuminova.mmce.common.tile.MEPatternProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = MEPatternProvider.class, remap = false)
public abstract class MixinMEPatternProvider implements RCIConfigurableObject {
    @Shadow
    public abstract DualityInterface getInterfaceDuality();

    @Override
    public RCIConfigManager r$getConfigManager() {
        return ((RCIConfigurableObject) this.getInterfaceDuality()).r$getConfigManager();
    }
}