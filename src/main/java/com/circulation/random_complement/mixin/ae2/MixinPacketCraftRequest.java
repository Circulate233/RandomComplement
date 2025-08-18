package com.circulation.random_complement.mixin.ae2;

import appeng.container.implementations.ContainerCraftAmount;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.GuiBridge;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.PacketCraftRequest;
import appeng.util.Platform;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.common.network.ContainerRollBACK;
import com.circulation.random_complement.common.util.RCAEBaseContainer;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Mixin(value = PacketCraftRequest.class,remap = false)
public class MixinPacketCraftRequest {

    @Shadow
    @Final
    private boolean heldShift;

    @Redirect(method = "serverPacketData", at = @At(value = "INVOKE", target = "Lappeng/util/Platform;openGUI(Lnet/minecraft/entity/player/EntityPlayer;ILappeng/core/sync/GuiBridge;Z)V"))
    public void serverPacketData(@Nonnull EntityPlayer p, int slot, @Nonnull GuiBridge type, boolean isBauble) {
        Container old = null;
        if (p.openContainer instanceof RCAEBaseContainer c) {
            old = c.rc$getOldContainer();
        }

        Platform.openGUI(p, slot, type, isBauble);

        if (p.openContainer instanceof RCAEBaseContainer c && old != null) {
            c.rc$setOldContainer(old);
        }
    }

    @Redirect(method = "serverPacketData", at = @At(value = "INVOKE", target = "Lappeng/container/implementations/ContainerCraftAmount;detectAndSendChanges()V"))
    public void detectAndSendChanges(ContainerCraftAmount instance, @Local(name = "ccc") ContainerCraftConfirm ccc, @Local(name = "player") EntityPlayer player) {
        ccc.detectAndSendChanges();
    }

    @Inject(method = "serverPacketData", at = @At("TAIL"))
    public void ContainerRollBACK(INetworkInfo manager, AppEngPacket packet, EntityPlayer player, CallbackInfo ci) {
        if (!heldShift) return;
        if (player.openContainer instanceof ContainerCraftConfirm) {
            if (player instanceof EntityPlayerMP entityPlayerMP) {
                entityPlayerMP.getServer().addScheduledTask(() -> {
                    var newContainer = entityPlayerMP.openContainer;
                    if (newContainer instanceof RCAEBaseContainer rac) {
                        var oldContainer = rac.rc$getOldContainer();
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
                    }
                    RandomComplement.NET_CHANNEL.sendTo(new ContainerRollBACK(), entityPlayerMP);
                });
            }
        }
    }
}
