package com.circulation.random_complement.mixin.ae2.container;

import appeng.container.implementations.ContainerCraftConfirm;
import com.circulation.random_complement.common.interfaces.RCAEBaseContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerCraftConfirm.class, remap = false)
public abstract class MixinContainerCraftConfirm implements RCAEBaseContainer {

    @Shadow
    public abstract void setAutoStart(boolean autoStart);

    @Inject(
        method = "startJob()V",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/crafting/ICraftingGrid;submitJob(Lappeng/api/networking/crafting/ICraftingJob;Lappeng/api/networking/crafting/ICraftingRequester;Lappeng/api/networking/crafting/ICraftingCPU;ZLappeng/api/networking/security/IActionSource;)Lappeng/api/networking/crafting/ICraftingLink;",
            shift = At.Shift.AFTER
        ),
        cancellable = true)
    public void startJob0(CallbackInfo ci) {
        if (this.rc$getOldContainer() != null) {
            this.setAutoStart(false);
            ci.cancel();
        }
    }
}