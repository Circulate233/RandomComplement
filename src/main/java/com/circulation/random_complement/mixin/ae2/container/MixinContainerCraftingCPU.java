package com.circulation.random_complement.mixin.ae2.container;

import appeng.api.parts.IPart;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.AEBaseContainer;
import appeng.container.guisync.GuiSync;
import appeng.container.implementations.ContainerCraftingCPU;
import appeng.helpers.ICustomNameObject;
import com.circulation.random_complement.common.interfaces.getCraftingCPUCluster;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerCraftingCPU.class, remap = false)
public abstract class MixinContainerCraftingCPU extends AEBaseContainer implements IMEMonitorHandlerReceiver<IAEItemStack>, ICustomNameObject, getCraftingCPUCluster {

    @GuiSync(1)
    @Unique
    public long randomComplement$eta2 = -1L;

    public MixinContainerCraftingCPU(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    /**
     * @author sddsd2332
     * @reason 添加合成已经耗时时间
     */
    @Inject(method = "detectAndSendChanges", at = @At(value = "INVOKE", target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;getRemainingItemCount()J"))
    public void getElapsedTime(CallbackInfo ci, @Local(name = "elapsedTime") long elapsedTime) {
        randomComplement$eta2 = elapsedTime;
    }

    @Override
    public long randomComplement$elapsedTime() {
        return randomComplement$eta2;
    }

}