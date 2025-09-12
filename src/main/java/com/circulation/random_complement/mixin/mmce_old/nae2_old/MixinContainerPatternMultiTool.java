package com.circulation.random_complement.mixin.mmce_old.nae2_old;

import appeng.helpers.IInterfaceHost;
import co.neeve.nae2.common.containers.ContainerPatternMultiTool;
import co.neeve.nae2.common.enums.PatternMultiToolInventories;
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

    @Shadow
    public PatternMultiToolInventories viewingInventory;

    @Inject(method = "getPatternInventory",at = @At("HEAD"), cancellable = true)
    public void getPatternInventoryMixin(CallbackInfoReturnable<IItemHandler> cir) {
        if (this.viewingInventory == PatternMultiToolInventories.INTERFACE && iface instanceof MEPatternProvider mep){
            cir.setReturnValue(mep.getPatterns());
        }
    }

    @Inject(method = "getInstalledCapacityUpgrades",at = @At("HEAD"), cancellable = true)
    public void getInstalledCapacityUpgrades(CallbackInfoReturnable<Integer> cir) {
        if (this.viewingInventory == PatternMultiToolInventories.INTERFACE && iface instanceof MEPatternProvider){
            cir.setReturnValue(3);
        }
    }

}
