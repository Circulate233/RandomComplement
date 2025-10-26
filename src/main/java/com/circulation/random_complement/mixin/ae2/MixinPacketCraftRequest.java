package com.circulation.random_complement.mixin.ae2;

import appeng.container.implementations.ContainerCraftAmount;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.GuiBridge;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.PacketCraftRequest;
import appeng.util.Platform;
import baubles.api.BaublesApi;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.common.interfaces.RCAEBaseContainer;
import com.circulation.random_complement.common.network.ContainerRollBACK;
import com.circulation.random_complement.common.util.MEHandler;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Mixin(value = PacketCraftRequest.class, remap = false)
public class MixinPacketCraftRequest {

    @Shadow
    @Final
    private boolean heldShift;

    @Unique
    @Optional.Method(modid = "baubles")
    private static ItemStack r$readBaubles(EntityPlayer player, int slot) {
        return BaublesApi.getBaublesHandler(player).getStackInSlot(slot);
    }

    @Redirect(method = "serverPacketData", at = @At(value = "INVOKE", target = "Lappeng/util/Platform;openGUI(Lnet/minecraft/entity/player/EntityPlayer;ILappeng/core/sync/GuiBridge;Z)V"))
    public void serverPacketData(@Nonnull EntityPlayer p, int slot, @Nonnull GuiBridge type, boolean isBauble) {
        if (heldShift) {
            if (p instanceof EntityPlayerMP entityPlayerMP) {
                Container old = null;
                if (p.openContainer instanceof RCAEBaseContainer c) {
                    old = c.rc$getOldContainer();
                }

                entityPlayerMP.getNextWindowId();
                entityPlayerMP.closeContainer();
                int windowId = entityPlayerMP.currentWindowId;
                var newContainer = new ContainerCraftConfirm(p.inventory,
                        MEHandler.getTerminalGuiObject(
                                isBauble ? r$readBaubles(p, slot) : p.inventory.getStackInSlot(slot),
                                p, slot, isBauble ? 1 : 0
                        ));
                entityPlayerMP.openContainer = newContainer;
                entityPlayerMP.openContainer.windowId = windowId;

                if (newContainer instanceof RCAEBaseContainer c && old != null) {
                    c.rc$setOldContainer(old);
                }
            }
        } else {
            if (p instanceof EntityPlayerMP entityPlayerMP) {
                Container old = null;
                if (p.openContainer instanceof RCAEBaseContainer c) {
                    old = c.rc$getOldContainer();
                }

                Platform.openGUI(p, slot, type, isBauble);

                if (p.openContainer instanceof RCAEBaseContainer c && old != null) {
                    c.rc$setOldContainer(old);
                }
            }
        }
    }

    @Redirect(method = "serverPacketData", at = @At(value = "INVOKE", target = "Lappeng/container/implementations/ContainerCraftAmount;detectAndSendChanges()V"))
    public void detectAndSendChanges(ContainerCraftAmount instance, @Local(name = "ccc") ContainerCraftConfirm ccc) {
        ccc.detectAndSendChanges();
    }

    @Inject(method = "serverPacketData", at = @At("TAIL"))
    public void ContainerRollBACK(INetworkInfo manager, AppEngPacket packet, EntityPlayer player, CallbackInfo ci) {
        if (!heldShift) return;
        if (player.openContainer instanceof RCAEBaseContainer rac) {
            if (player instanceof EntityPlayerMP entityPlayerMP) {
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
                RandomComplement.NET_CHANNEL.sendTo(new ContainerRollBACK(), entityPlayerMP);
            }
        }
    }
}