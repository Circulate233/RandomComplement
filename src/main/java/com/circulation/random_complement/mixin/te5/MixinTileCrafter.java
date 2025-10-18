package com.circulation.random_complement.mixin.te5;

import cofh.thermalexpansion.block.machine.TileCrafter;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import com.circulation.random_complement.RCConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileCrafter.class, remap = false)
public abstract class MixinTileCrafter extends TileMachineBase {

    /**
     * @author circulation
     * @reason 不允许输出槽有物品的情况下工作
     */
    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    protected void canStartMixin(CallbackInfoReturnable<Boolean> cir) {
        if (RCConfig.TE5.SequentialFabricatorMixin && !this.getStackInSlot(TileCrafter.SLOT_OUTPUT).isEmpty()) {
            cir.setReturnValue(false);
        }
    }

}
