package com.circulation.random_complement.mixin.ae2.miss_craft;

import appeng.api.networking.crafting.ICraftingJob;
import appeng.container.implementations.ContainerCraftConfirm;
import com.circulation.random_complement.common.interfaces.RCAEBaseContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ContainerCraftConfirm.class, remap = false)
public abstract class MixinContainerCraftConfirm implements RCAEBaseContainer {

    @Redirect(method = "detectAndSendChanges", at = @At(value = "INVOKE", target = "Lappeng/api/networking/crafting/ICraftingJob;isSimulation()Z", ordinal = 1))
    public boolean isSimulation1(ICraftingJob instance) {
        return true;
    }

    @Redirect(method = "detectAndSendChanges", at = @At(value = "INVOKE", target = "Lappeng/api/networking/crafting/ICraftingJob;isSimulation()Z", ordinal = 2))
    public boolean isSimulation2(ICraftingJob instance) {
        return true;
    }
}