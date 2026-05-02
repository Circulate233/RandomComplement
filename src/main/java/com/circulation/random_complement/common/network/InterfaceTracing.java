package com.circulation.random_complement.common.network;

import appeng.api.implementations.IUpgradeableHost;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.DimensionalCoord;
import appeng.core.localization.PlayerMessages;
import appeng.me.cache.CraftingGridCache;
import appeng.me.helpers.IGridProxyable;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.handler.HighlighterHandler;
import com.circulation.random_complement.common.interfaces.Packet;
import com.circulation.random_complement.mixin.ae2.AccessorCraftingCPUCluster;
import com.circulation.random_complement.mixin.ae2.container.AccessorContainerCraftingCPU;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class InterfaceTracing implements Packet<InterfaceTracing> {

    private IAEItemStack item;
    private int[] dims;
    private BlockPos[] poss;

    public InterfaceTracing() {
    }

    public InterfaceTracing(@NotNull IAEItemStack stack) {
        this.item = stack;
    }

    public InterfaceTracing(@NotNull List<DimensionalCoord> positions) {
        int n = positions.size();
        this.dims = new int[n];
        this.poss = new BlockPos[n];
        for (int i = 0; i < n; i++) {
            DimensionalCoord coord = positions.get(i);
            this.dims[i] = coord.getWorld().provider.getDimension();
            this.poss[i] = coord.getPos();
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            item = AEItemStack.fromPacket(buf);
        } else {
            int n = buf.readInt();
            dims = new int[n];
            poss = new BlockPos[n];
            for (int i = 0; i < n; i++) {
                dims[i] = buf.readInt();
                poss[i] = BlockPos.fromLong(buf.readLong());
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        boolean carriesItem = item != null;
        buf.writeBoolean(carriesItem);
        try {
            if (carriesItem) {
                item.writeToPacket(buf);
            } else {
                int n = poss == null ? 0 : poss.length;
                buf.writeInt(n);
                for (int i = 0; i < n; i++) {
                    buf.writeInt(dims[i]);
                    buf.writeLong(poss[i].toLong());
                }
            }
        } catch (IOException e) {
            RandomComplement.LOGGER.warn("InterfaceTracing.toBytes failed", e);
        }
    }

    @Override
    public IMessage onMessage(InterfaceTracing message, MessageContext ctx) {
        switch (ctx.side) {
            case SERVER -> {
                if (message.item == null) return null;
                val player = ctx.getServerHandler().player;
                if (!(player.openContainer instanceof AccessorContainerCraftingCPU container)) return null;
                Object monitor = container.invokerGetMonitor();
                if (!(monitor instanceof AccessorCraftingCPUCluster cpu)) return null;
                val grid = container.invokerGetNetwork();
                if (grid == null) return null;
                final CraftingGridCache c = grid.getCache(ICraftingGrid.class);
                if (c == null) return null;
                val list = new ObjectArrayList<ICraftingMedium>();
                for (var detail : cpu.getTasks().keySet()) {
                    var outputs = detail.getCondensedOutputs();
                    for (IAEItemStack output : outputs) {
                        if (message.item.equals(output)) {
                            var mediums = c.getMediums(detail);
                            if (mediums != null) list.addAll(mediums);
                            break;
                        }
                    }
                }
                if (list.isEmpty()) return null;
                val coords = new ObjectArrayList<DimensionalCoord>();
                for (var icm : list) {
                    Object obj = icm;
                    if (icm instanceof IUpgradeableHost iuh) obj = iuh.getTile();
                    if (obj instanceof IGridProxyable igp) {
                        DimensionalCoord loc = igp.getLocation();
                        if (loc != null && loc.getWorld() != null) {
                            coords.add(loc);
                        }
                    }
                }
                if (coords.isEmpty()) return null;
                RandomComplement.NET_CHANNEL.sendTo(new InterfaceTracing(coords), player);
                player.closeScreen();
            }
            case CLIENT -> onClient(message);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void onClient(InterfaceTracing message) {
        if (message.poss == null || message.poss.length == 0) return;
        var mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;
        int playerDim = mc.world.provider.getDimension();
        BlockPos[] highlight = new BlockPos[message.poss.length];
        boolean any = false;
        for (int i = 0; i < message.poss.length; i++) {
            BlockPos blockPos = message.poss[i];
            int interfaceDim = message.dims[i];
            if (playerDim != interfaceDim) {
                try {
                    var w = DimensionManager.getWorld(interfaceDim);
                    String name = w != null
                        ? w.provider.getDimensionType().getName()
                        : DimensionType.getById(interfaceDim).getName();
                    mc.player.sendStatusMessage(PlayerMessages.InterfaceInOtherDimParam.get(interfaceDim, name), false);
                } catch (Exception ex) {
                    mc.player.sendStatusMessage(PlayerMessages.InterfaceInOtherDim.get(), false);
                }
            } else {
                highlight[i] = blockPos;
                mc.player.sendStatusMessage(PlayerMessages.InterfaceHighlighted.get(blockPos.getX(), blockPos.getY(), blockPos.getZ()), false);
                any = true;
            }
        }
        if (any) HighlighterHandler.INSTANCE.hilightBlock(highlight, System.currentTimeMillis() + 20000L, playerDim);
    }
}