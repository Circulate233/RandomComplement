package com.circulation.random_complement.mixin.thaumicenergistics;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.util.Platform;
import com.circulation.random_complement.common.network.RCPacketMEInventoryUpdate;
import com.circulation.random_complement.common.util.SimpleItem;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumicenergistics.container.ContainerBaseTerminal;
import thaumicenergistics.container.ICraftingContainer;
import thaumicenergistics.container.part.ContainerArcaneTerminal;
import thaumicenergistics.part.PartBase;
import thaumicenergistics.part.PartSharedTerminal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(ContainerArcaneTerminal.class)
public abstract class MixinContainerArcaneTerminal extends ContainerBaseTerminal implements IMEMonitorHandlerReceiver<IAEItemStack>, ICraftingContainer {

    @Unique
    private boolean randomComplement$incomplete = true;

    @Shadow(remap = false)
    protected PartSharedTerminal part;

    public MixinContainerArcaneTerminal(EntityPlayer player, PartBase part) {
        super(player, part);
    }

    @Unique
    private static final Set<SimpleItem> randomComplement$craftableCacheS = new ObjectOpenHashSet<>();

    @Unique
    public Set<SimpleItem> randomComplement$craftableCache = new ObjectOpenHashSet<>();

    @Unique
    private Set<SimpleItem> randomComplement$mergedCache = new ObjectOpenHashSet<>();

    @Unique
    private int randomComplement$a = 0;

    @Inject(method = "detectAndSendChanges",at = @At(value = "INVOKE", target = "Lthaumicenergistics/part/PartSharedTerminal;getTile()Lnet/minecraft/tileentity/TileEntity;",remap = false))
    public void detectAndSendChangesMixin(CallbackInfo ci) {
        if (randomComplement$a == 0) {
            randomComplement$a++;
            return;
        }
        if (randomComplement$incomplete) {
            randomComplement$queueInventory((EntityPlayerMP) this.player,4);
            randomComplement$incomplete = false;
        }
    }

    @Inject(method = "onContainerClosed",at = @At("TAIL"))
    public void onClosedMixin(EntityPlayer player, CallbackInfo ci){
        if (Platform.isServer()) {
            randomComplement$queueInventory((EntityPlayerMP) this.player,5);
        }
    }

    @Unique
    private void randomComplement$queueInventory(EntityPlayerMP playerMP,int id) {
        try {
            var piu = new RCPacketMEInventoryUpdate((short)id);
            List<IAEItemStack> items = new ArrayList<>();
            var node = part.getGridNode();
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
                    piu = new RCPacketMEInventoryUpdate((short)id);
                    piu.appendItem(send);
                }
            }

            NetworkHandler.instance().sendTo(piu, playerMP);
        } catch (IOException e) {
            AELog.debug(e);
        }
    }

}
