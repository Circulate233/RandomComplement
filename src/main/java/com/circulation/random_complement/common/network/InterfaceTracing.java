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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class InterfaceTracing implements Packet<InterfaceTracing> {

    private IAEItemStack item;
    private List<DimensionalCoord> positions;
    private boolean server;

    public InterfaceTracing() {
        item = null;
    }

    public InterfaceTracing(@NotNull IAEItemStack stack) {
        item = stack;
        server = true;
    }

    public InterfaceTracing(@NotNull List<DimensionalCoord> positions) {
        this.positions = positions;
        server = false;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean())
            item = AEItemStack.fromPacket(buf);
        else {
            positions = new ObjectArrayList<>();
            var nbt = Objects.requireNonNull(ByteBufUtils.readTag(buf)).getTagList("poss", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < nbt.tagCount(); i++) {
                NBTTagCompound tag = nbt.getCompoundTagAt(i);
                positions.add(new DimensionalCoord(
                    DimensionManager.getWorld(tag.getInteger("dim")),
                    BlockPos.fromLong(tag.getLong("pos"))
                ));
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(server);
        try {
            if (server) {
                item.writeToPacket(buf);
            } else {
                var nbt = new NBTTagCompound();
                var list = new NBTTagList();
                for (DimensionalCoord coord : positions) {
                    var p = new NBTTagCompound();
                    p.setInteger("dim", coord.getWorld().provider.getDimension());
                    p.setLong("pos", coord.getPos().toLong());
                    list.appendTag(p);
                }
                nbt.setTag("poss", list);
                ByteBufUtils.writeTag(buf, nbt);
            }
        } catch (IOException ignored) {

        }
    }

    @Override
    public IMessage onMessage(InterfaceTracing message, MessageContext ctx) {
        switch (ctx.side) {
            case SERVER -> {
                val player = ctx.getServerHandler().player;
                if (player.openContainer instanceof AccessorContainerCraftingCPU container) {
                    val cpu = (AccessorCraftingCPUCluster) (Object) container.invokerGetMonitor();
                    if (cpu == null) return null;
                    val grid = container.invokerGetNetwork();
                    final CraftingGridCache c = grid.getCache(ICraftingGrid.class);
                    val list = new ObjectArrayList<ICraftingMedium>();
                    for (var detail : cpu.getTasks().keySet()) {
                        var outputs = detail.getCondensedOutputs();
                        for (IAEItemStack output : outputs) {
                            if (message.item.equals(output)) {
                                list.addAll(c.getMediums(detail));
                                break;
                            }
                        }
                    }
                    if (list.isEmpty()) return null;
                    val poss = new ObjectArrayList<DimensionalCoord>();
                    for (var icm : list) {
                        Object obj = icm;
                        if (icm instanceof IUpgradeableHost iuh) obj = iuh.getTile();
                        if (obj instanceof IGridProxyable igp) {
                            poss.add(igp.getLocation());
                        }
                    }
                    RandomComplement.NET_CHANNEL.sendTo(new InterfaceTracing(poss), player);
                    player.closeScreen();
                }
            }
            case CLIENT -> onClient(message);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void onClient(InterfaceTracing message) {
        var positions = message.positions;
        if (positions == null || positions.isEmpty()) return;
        var mc = Minecraft.getMinecraft();
        int playerDim = mc.world.provider.getDimension();
        BlockPos[] poss = new BlockPos[positions.size()];
        boolean b = false;
        for (var i = 0; i < positions.size(); i++) {
            var coord = positions.get(i);
            BlockPos blockPos = coord.getPos();
            int interfaceDim = coord.getWorld().provider.getDimension();
            if (playerDim != interfaceDim) {
                try {
                    mc.player.sendStatusMessage(PlayerMessages.InterfaceInOtherDimParam.get(interfaceDim, DimensionManager.getWorld(interfaceDim).provider.getDimensionType().getName()), false);
                } catch (Exception var8) {
                    mc.player.sendStatusMessage(PlayerMessages.InterfaceInOtherDim.get(), false);
                }
            } else {
                poss[i] = blockPos;
                mc.player.sendStatusMessage(PlayerMessages.InterfaceHighlighted.get(blockPos.getX(), blockPos.getY(), blockPos.getZ()), false);
                b = true;
            }
        }
        if (b)
            HighlighterHandler.INSTANCE.hilightBlock(poss, System.currentTimeMillis() + 20000L, playerDim);
    }
}