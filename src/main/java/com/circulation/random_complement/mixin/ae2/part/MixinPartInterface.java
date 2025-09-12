package com.circulation.random_complement.mixin.ae2.part;

import appeng.helpers.DualityInterface;
import appeng.parts.misc.PartInterface;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PartInterface.class,remap = false)
public abstract class MixinPartInterface implements RCIConfigurableObject {
    @Shadow
    public abstract DualityInterface getInterfaceDuality();

    @Override
    public RCIConfigManager r$getConfigManager() {
        return ((RCIConfigurableObject) this.getInterfaceDuality()).r$getConfigManager();
    }
}