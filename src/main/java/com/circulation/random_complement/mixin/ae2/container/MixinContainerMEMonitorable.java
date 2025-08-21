package com.circulation.random_complement.mixin.ae2.container;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.parts.IPart;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.IConfigurableObject;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import com.circulation.random_complement.common.interfaces.RCAEBaseContainer;
import com.circulation.random_complement.common.network.RCPacketMEInventoryUpdate;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = ContainerMEMonitorable.class)
public abstract class MixinContainerMEMonitorable extends AEBaseContainer implements IConfigManagerHost, IConfigurableObject, IMEMonitorHandlerReceiver<IAEItemStack>, RCAEBaseContainer {

    @Shadow(remap = false)
    @Final
    public IItemList<IAEItemStack> items;

    @Shadow(remap = false)
    private IGridNode networkNode;

    @Unique
    @SuppressWarnings("FieldCanBeLocal")
    private Container rc$oldContainer;

    public MixinContainerMEMonitorable(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    @Unique
    private boolean randomComplement$incomplete = true;

    @Inject(method = "detectAndSendChanges",at = @At("TAIL"))
    public void detectAndSendChangesMixin(CallbackInfo ci) {
        if (Platform.isServer() && randomComplement$incomplete) {
            randomComplement$queueInventory((EntityPlayerMP) this.getInventoryPlayer().player,1);
            randomComplement$incomplete = false;
        }
    }

    @Inject(method = "onContainerClosed",at = @At("TAIL"))
    public void onClosedMixin(EntityPlayer player, CallbackInfo ci){
        if (Platform.isServer()) {
            randomComplement$queueInventory((EntityPlayerMP) this.getInventoryPlayer().player,2);
        }
    }

    @Unique
    private void randomComplement$queueInventory(EntityPlayerMP playerMP,int id) {
        try {
            var piu = new RCPacketMEInventoryUpdate((short) id);
            List<IAEItemStack> items = new ArrayList<>();
            var node = this.networkNode;
            if (node == null) {
                return;
            } else {
                IGrid grid = node.getGrid();
                ICraftingGrid cc = grid.getCache(ICraftingGrid.class);
                ImmutableSet<ICraftingCPU> cpuSet = cc.getCpus();

                for (ICraftingCPU c : cpuSet) {
                    if (c.isBusy()) {
                        var item = c.getFinalOutput();
                        if (item != null) {
                            items.add(item);
                        }
                    }
                }
            }
            if (items.isEmpty())return;
            for (IAEItemStack send : items) {
                try {
                    piu.appendItem(send);
                } catch (BufferOverflowException var7) {
                    NetworkHandler.instance().sendTo(piu,playerMP);
                    piu = new RCPacketMEInventoryUpdate((short) id);
                    piu.appendItem(send);
                }
            }

            NetworkHandler.instance().sendTo(piu, playerMP);
        } catch (IOException e) {
            AELog.debug(e);
        }
    }

    @Unique
    @Override
    public void rc$setOldContainer(Container old) {
        rc$oldContainer = old;
    }

    @Unique
    @Override
    public Container rc$getOldContainer() {
        return rc$oldContainer;
    }
}
