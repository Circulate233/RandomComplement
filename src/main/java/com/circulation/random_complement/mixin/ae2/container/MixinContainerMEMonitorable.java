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
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import com.circulation.random_complement.common.interfaces.SpecialPacket;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
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
public abstract class MixinContainerMEMonitorable extends AEBaseContainer implements IConfigManagerHost, IConfigurableObject, IMEMonitorHandlerReceiver<IAEItemStack> {

    @Shadow(remap = false)
    @Final
    public IItemList<IAEItemStack> items;

    @Shadow(remap = false)
    private IGridNode networkNode;

    public MixinContainerMEMonitorable(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    @Unique
    private boolean randomComplement$incomplete = true;

    @Inject(method = "detectAndSendChanges",at = @At("TAIL"))
    public void detectAndSendChangesMixin(CallbackInfo ci) {
        if (Platform.isServer() && randomComplement$incomplete) {
            randomComplement$queueInventory((EntityPlayerMP) this.getInventoryPlayer().player);
            randomComplement$incomplete = false;
        }
    }

    @Unique
    private void randomComplement$queueInventory(EntityPlayerMP playerMP) {
        try {
            PacketMEInventoryUpdate piu = new PacketMEInventoryUpdate();
            ((SpecialPacket)piu).r$setId(2);
            List<IAEItemStack> items = new ArrayList<>();
            var node = this.networkNode;
            if (node == null) {
                return;
            } else {
                IGrid grid = node.getGrid();
                ICraftingGrid cc = grid.getCache(ICraftingGrid.class);
                ImmutableSet<ICraftingCPU> cpuSet = cc.getCpus();

                for (ICraftingCPU c : cpuSet) {
                    var item = c.getFinalOutput();
                    if (item != null){
                        items.add(item);
                    }
                }
            }
            if (items.isEmpty())return;
            for (IAEItemStack send : items) {
                try {
                    piu.appendItem(send);
                } catch (BufferOverflowException var7) {
                    NetworkHandler.instance().sendTo(piu,playerMP);
                    piu = new PacketMEInventoryUpdate();
                    ((SpecialPacket)piu).r$setId(1);
                    piu.appendItem(send);
                }
            }

            NetworkHandler.instance().sendTo(piu, playerMP);
        } catch (IOException e) {
            AELog.debug(e);
        }
    }
}
