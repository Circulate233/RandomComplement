package com.circulation.random_complement.mixin.mmce.nae2;

import appeng.helpers.IInterfaceHost;
import co.neeve.nae2.common.containers.ContainerPatternMultiTool;
import github.kasuminova.mmce.common.tile.MEPatternProvider;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ContainerPatternMultiTool.class,remap = false)
public class MixinContainerPatternMultiTool {

    @Shadow
    @Final
    private IInterfaceHost iface;

    @Inject(method = "getPatternInventory",at = @At("RETURN"), cancellable = true)
    public void getPatternInventoryMixin(CallbackInfoReturnable<IItemHandler> cir) {
        if (iface instanceof MEPatternProvider mep){
            cir.setReturnValue(mep.getPatterns());
        }
    }

}
