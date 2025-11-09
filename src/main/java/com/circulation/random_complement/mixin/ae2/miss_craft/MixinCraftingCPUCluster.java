package com.circulation.random_complement.mixin.ae2.miss_craft;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import com.circulation.random_complement.common.interfaces.RCCraftingJob;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class MixinCraftingCPUCluster {

    @Shadow
    private IAEItemStack finalOutput;

    @Unique
    private IAEItemStack r$waitInput;

    @Shadow
    protected abstract void completeJob();

    @Shadow
    protected abstract void updateCPU();

    @WrapOperation(method = "injectItems", at = @At(value = "INVOKE", target = "Ljava/lang/Object;equals(Ljava/lang/Object;)Z"))
    public boolean injectItems(Object instance, Object o, Operation<Boolean> original, @Local(name = "type") Actionable type) {
        if (r$waitInput != null && r$waitInput.equals(o)) {
            if (type == Actionable.MODULATE) {
                var size = ((IAEItemStack) o).getStackSize();
                if (r$waitInput.getStackSize() <= size) {
                    r$waitInput = null;
                } else {
                    r$waitInput.decStackSize(size);
                }
                finalOutput.decStackSize(size);
            }
            return false;
        }
        return original.call(instance,o);
    }

    @Inject(method = "injectItems", at = @At("RETURN"))
    public void onStop(IAEItemStack input, Actionable type, IActionSource src, CallbackInfoReturnable<IAEItemStack> cir) {
        if (this.finalOutput != null && this.finalOutput.getStackSize() <= 0) {
            this.completeJob();
            this.updateCPU();
        }
    }

    @Inject(method = "submitJob", at = @At(value = "INVOKE", target = "Lappeng/crafting/CraftingTreeNode;setJob(Lappeng/crafting/MECraftingInventory;Lappeng/me/cluster/implementations/CraftingCPUCluster;Lappeng/api/networking/security/IActionSource;)V"))
    public void submitJob(IGrid g, ICraftingJob job, IActionSource src, ICraftingRequester requestingMachine, CallbackInfoReturnable<ICraftingLink> cir) {
        var s = ((RCCraftingJob) job).getWaitingItem();
        if (s != null && s.getStackSize() > 0) {
            r$waitInput = s.copy();
            s.reset();
        }
    }

}