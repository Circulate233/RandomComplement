package com.circulation.random_complement.mixin.mmce;

import appeng.helpers.IInterfaceHost;
import com.circulation.random_complement.common.interfaces.RCIInterfaceHostHelper;
import github.kasuminova.mmce.common.container.ContainerMEPatternProvider;
import github.kasuminova.mmce.common.tile.MEPatternProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ContainerMEPatternProvider.class, remap = false)
public abstract class MixinContainerMEPatternProvider implements RCIInterfaceHostHelper {

    @Shadow
    @Final
    private MEPatternProvider owner;

    @Override
    public IInterfaceHost r$getTarget() {
        return this.owner;
    }

}