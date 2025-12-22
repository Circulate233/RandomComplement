package com.circulation.random_complement.mixin.ae2;

import appeng.container.implementations.ContainerCraftAmount;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.core.sync.GuiBridge;
import appeng.core.sync.packets.PacketCraftRequest;
import baubles.api.BaublesApi;
import com.circulation.random_complement.common.interfaces.RCAEBaseContainer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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

    @WrapOperation(method = "serverPacketData", at = @At(value = "INVOKE", target = "Lappeng/util/Platform;openGUI(Lnet/minecraft/entity/player/EntityPlayer;ILappeng/core/sync/GuiBridge;Z)V"))
    public void serverPacketData(@Nonnull EntityPlayer p, int slot, @Nonnull GuiBridge type, boolean isBauble, Operation<Void> original) {
        if (p instanceof EntityPlayerMP entityPlayerMP) {
            Container old = null;
            if (p.openContainer instanceof RCAEBaseContainer c) {
                old = c.rc$getOldContainer();
            }

            original.call(p, slot, type, isBauble);

            if (p.openContainer instanceof RCAEBaseContainer c && old != null) {
                c.rc$setOldContainer(old);
            }
        }
    }

    @Redirect(method = "serverPacketData", at = @At(value = "INVOKE", target = "Lappeng/container/implementations/ContainerCraftAmount;detectAndSendChanges()V"))
    public void detectAndSendChanges(ContainerCraftAmount instance, @Local(name = "ccc") ContainerCraftConfirm ccc) {
        ccc.detectAndSendChanges();
    }
}