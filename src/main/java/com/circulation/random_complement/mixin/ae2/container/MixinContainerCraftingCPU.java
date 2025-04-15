package com.circulation.random_complement.mixin.ae2.container;

import appeng.api.parts.IPart;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerCraftingCPU;
import appeng.helpers.ICustomNameObject;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ContainerCraftingCPU.class,remap = false)
public abstract class MixinContainerCraftingCPU extends AEBaseContainer implements IMEMonitorHandlerReceiver<IAEItemStack>, ICustomNameObject {

    @Shadow
    protected abstract void setEstimatedTime(long eta);

    @Shadow
    abstract CraftingCPUCluster getMonitor();

    public MixinContainerCraftingCPU(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    /**
     * @author sddsd2332
     * @reason 合成情况预计时间修改为已经耗时时间
     *
     */
    @Redirect(method = "detectAndSendChanges", at = @At(value = "INVOKE", target = "Lappeng/container/implementations/ContainerCraftingCPU;setEstimatedTime(J)V"))
    public void setEstimatedTime(ContainerCraftingCPU instance, long eta) {
        setEstimatedTime(getMonitor().getElapsedTime());
    }
}
