package com.circulation.random_complement.mixin.ae2.miss_craft;

import appeng.api.networking.crafting.ICraftingJob;
import appeng.container.implementations.ContainerCraftConfirm;
import com.circulation.random_complement.common.interfaces.RCAEBaseContainer;
import com.circulation.random_complement.common.interfaces.RCCraftingJob;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Mixin(ContainerCraftConfirm.class)
public abstract class MixinContainerCraftConfirm implements RCAEBaseContainer {

    @Unique
    private boolean r$canIgnoredInput = false;

    @WrapOperation(method = "detectAndSendChanges", at = @At(value = "INVOKE", target = "Lappeng/api/networking/crafting/ICraftingJob;isSimulation()Z", ordinal = 1, remap = false))
    public boolean isSimulation1(ICraftingJob instance, Operation<Boolean> original) {
        if (!r$canIgnoredInput) return original.call(instance);
        return true;
    }

    @WrapOperation(method = "detectAndSendChanges", at = @At(value = "INVOKE", target = "Lappeng/api/networking/crafting/ICraftingJob;isSimulation()Z", ordinal = 2, remap = false))
    public boolean isSimulation2(ICraftingJob instance, Operation<Boolean> original) {
        if (!r$canIgnoredInput) return original.call(instance);
        return true;
    }

    @Inject(method = "setJob", at = @At("HEAD"), remap = false)
    public void setJob(Future<ICraftingJob> job, CallbackInfo ci) {
        if (job == null) return;
        try {
            if (job.get() instanceof RCCraftingJob j) {
                this.r$canIgnoredInput = j.canIgnoredInput();
            }
        } catch (InterruptedException | ExecutionException ignored) {
        }
    }
}