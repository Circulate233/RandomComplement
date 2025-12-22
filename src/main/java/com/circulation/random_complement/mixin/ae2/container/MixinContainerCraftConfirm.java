package com.circulation.random_complement.mixin.ae2.container;

import appeng.container.implementations.ContainerCraftConfirm;
import appeng.core.sync.GuiBridge;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.common.interfaces.RCAEBaseContainer;
import com.circulation.random_complement.common.network.ContainerRollBACK;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ContainerCraftConfirm.class, remap = false, priority = 998)
public abstract class MixinContainerCraftConfirm extends MixinAEBaseContainer implements RCAEBaseContainer {

    @WrapOperation(method = "startJob", at = @At(value = "INVOKE", target = "Lappeng/util/Platform;openGUI(Lnet/minecraft/entity/player/EntityPlayer;ILappeng/core/sync/GuiBridge;Z)V"))
    public void backGui(EntityPlayer extra, int obj, GuiBridge opener, boolean p, Operation<Void> original) {
        if (this.rc$getOldContainer() != null) {
            if (this.getPlayerInv().player instanceof EntityPlayerMP entityPlayerMP) {
                var oldContainer = this.rc$getOldContainer();
                if (oldContainer instanceof ContainerPlayer) {
                    entityPlayerMP.closeContainer();
                } else {
                    entityPlayerMP.getNextWindowId();
                    entityPlayerMP.closeContainer();
                    int windowId = entityPlayerMP.currentWindowId;
                    entityPlayerMP.openContainer = oldContainer;
                    entityPlayerMP.openContainer.windowId = windowId;
                }
                RandomComplement.NET_CHANNEL.sendTo(new ContainerRollBACK(), entityPlayerMP);
                return;
            }
        }
        original.call(extra, obj, opener, p);
    }

}