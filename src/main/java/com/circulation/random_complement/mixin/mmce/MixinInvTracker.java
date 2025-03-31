package com.circulation.random_complement.mixin.mmce;

import com.circulation.random_complement.common.interfaces.SpecialInvTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "appeng.container.implementations.ContainerInterfaceTerminal$InvTracker",remap = false)
public class MixinInvTracker implements SpecialInvTracker {

    @Final
    @Shadow
    private String unlocalizedName;

    @Override
    public String r$getUnlocalizedName() {
        return unlocalizedName;
    }

}
