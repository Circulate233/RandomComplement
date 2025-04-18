package com.circulation.random_complement.mixin.ae2.container;

import appeng.api.parts.IPart;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.AEBaseContainer;
import appeng.container.guisync.GuiSync;
import appeng.container.implementations.ContainerCraftingCPU;
import appeng.helpers.ICustomNameObject;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import com.circulation.random_complement.common.integration.ae2.getCraftingCPUCluster;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ContainerCraftingCPU.class, remap = false)
public abstract class MixinContainerCraftingCPU extends AEBaseContainer implements IMEMonitorHandlerReceiver<IAEItemStack>, ICustomNameObject, getCraftingCPUCluster {


    public MixinContainerCraftingCPU(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    /**
     * @author sddsd2332
     * @reason 添加合成已经耗时时间
     */
    @Redirect(method = "detectAndSendChanges", at = @At(value = "INVOKE", target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;getElapsedTime()J"))
    public long getElapsedTime(CraftingCPUCluster instance) {
        eta2 = instance.getElapsedTime();
        return eta2;
    }

    @GuiSync(1)
    @Unique
    public long eta2 = -1L;

    @Override
    public long elapsedTime() {
        return eta2;
    }


}
