package com.circulation.random_complement.mixin.ae2.container;

import appeng.container.implementations.ContainerCraftConfirm;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.common.interfaces.RCAEBaseContainer;
import com.circulation.random_complement.common.network.ContainerRollBACK;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerCraftConfirm.class, remap = false)
public abstract class MixinContainerCraftConfirm extends MixinAEBaseContainer implements RCAEBaseContainer {

    @Shadow
    public abstract void setAutoStart(boolean autoStart);

    @Inject(
        method = "startJob",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/crafting/ICraftingGrid;submitJob(Lappeng/api/networking/crafting/ICraftingJob;Lappeng/api/networking/crafting/ICraftingRequester;Lappeng/api/networking/crafting/ICraftingCPU;ZLappeng/api/networking/security/IActionSource;)Lappeng/api/networking/crafting/ICraftingLink;",
            shift = At.Shift.AFTER
        ),
        cancellable = true)
    public void startJob0(CallbackInfo ci) {
        if (this.rc$getOldContainer() != null) {
            this.setAutoStart(false);
            if (this.getPlayerInv().player instanceof EntityPlayerMP entityPlayerMP) {
                var oldContainer = this.rc$getOldContainer();
                if (oldContainer != null) {
                    if (oldContainer instanceof ContainerPlayer) {
                        entityPlayerMP.closeContainer();
                    } else {
                        entityPlayerMP.getNextWindowId();
                        entityPlayerMP.closeContainer();
                        int windowId = entityPlayerMP.currentWindowId;
                        entityPlayerMP.openContainer = oldContainer;
                        entityPlayerMP.openContainer.windowId = windowId;
                    }
                }
                RandomComplement.NET_CHANNEL.sendTo(new ContainerRollBACK(), entityPlayerMP);
            }
            ci.cancel();
        }
    }

}