package com.circulation.random_complement.mixin.ae2fc;

import appeng.helpers.DualityInterface;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import com.glodblock.github.common.part.PartDualInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PartDualInterface.class, remap = false)
public abstract class MixinPartDualInterface implements RCIConfigurableObject {
    @Shadow
    public abstract DualityInterface getInterfaceDuality();

    @Override
    public RCIConfigManager r$getConfigManager() {
        return ((RCIConfigurableObject) this.getInterfaceDuality()).r$getConfigManager();
    }
}