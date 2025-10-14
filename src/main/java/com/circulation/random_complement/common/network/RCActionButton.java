package com.circulation.random_complement.common.network;

import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.items.misc.ItemEncodedPattern;
import appeng.tile.inventory.AppEngInternalInventory;
import com.circulation.random_complement.client.buttonsetting.Action;
import com.circulation.random_complement.common.interfaces.Packet;
import com.circulation.random_complement.common.interfaces.RCCraftingGridCache;
import com.circulation.random_complement.common.interfaces.RCIInterfaceHostHelper;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

public class RCActionButton implements Packet<RCActionButton> {

    private byte ordinal;

    public RCActionButton() {

    }

    public RCActionButton(Action action) {
        this.ordinal = (byte) action.ordinal();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ordinal = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.ordinal);
    }

    @Override
    public IMessage onMessage(RCActionButton message, MessageContext ctx) {
        val player = ctx.getServerHandler().player;
        val action = Action.values()[message.ordinal];
        switch (action) {
            case MULTIPLY_2, MULTIPLY_3, DIVIDE_2, DIVIDE_3 -> {
                if (player.openContainer instanceof RCIInterfaceHostHelper ci) {
                    val tile = ci.r$getTarget();
                    val duality = tile.getInterfaceDuality();
                    val gridNode = tile.getActionableNode();
                    val cgc = (RCCraftingGridCache) gridNode.getGrid().getCache(ICraftingGrid.class);
                    val patterns = (AppEngInternalInventory) duality.getPatterns();
                    for (byte i = 0; i < patterns.getSlots(); i++) {
                        val oldPattern = patterns.getStackInSlot(i);
                        val pattern = oldPattern.copy();
                        NBTTagCompound pNbt;
                        if (!pattern.isEmpty()
                                && pattern.getItem() instanceof ItemEncodedPattern ep
                                && pattern.hasTagCompound()
                                && !(pNbt = pattern.getTagCompound()).getBoolean("crafting")
                        ) {
                            val in = pNbt.getTagList("in", 10);
                            boolean success = true;
                            List<NBTBase> list = new ObjectArrayList<>(in.tagList);
                            list.addAll(pNbt.getTagList("out", 10).tagList);
                            if (pNbt.hasKey("Inputs")) {
                                list.addAll(pNbt.getTagList("Inputs", 10).tagList);
                                list.addAll(pNbt.getTagList("Outputs", 10).tagList);
                            }
                            for (NBTBase item : list) {
                                if (!(item instanceof NBTTagCompound nbt) || !nbt.hasKey("Count")) continue;
                                long oldSize = nbt.getLong("Count");
                                long newSize = r$quantityProcessing(oldSize, action);
                                if (!r$correctQuantity(oldSize, newSize, action)) {
                                    success = false;
                                    break;
                                }
                                if (success) {
                                    nbt.setInteger("Count", (int) newSize);
                                    if (nbt.hasKey("Cnt")) {
                                        oldSize = nbt.getLong("Cnt");
                                        newSize = r$quantityProcessing(oldSize, action);
                                        if (!r$correctQuantity(oldSize, newSize, action)) {
                                            success = false;
                                            break;
                                        }
                                        if (success)
                                            nbt.setInteger("Cnt", (int) newSize);
                                    }
                                }
                            }
                            if (success) {
                                patterns.extractItem(i, Integer.MAX_VALUE, false);
                                patterns.insertItem(i, pattern, false);
                                patterns.getTileEntity().saveChanges();
                                gridNode.getGrid().postEvent(new MENetworkCraftingPatternChange(tile, gridNode));
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Unique
    private long r$quantityProcessing(long size, Action action) {
        return switch (action) {
            case MULTIPLY_2 -> size << 1;
            case MULTIPLY_3 -> size * 3;
            case DIVIDE_2 -> size >> 1;
            case DIVIDE_3 -> size / 3;
            default -> throw new RuntimeException("Unreasonable calls");
        };
    }

    @Unique
    private boolean r$correctQuantity(long oldSize, long newSize, Action action) {
        return switch (action) {
            case MULTIPLY_2 -> newSize >> 1 == oldSize && newSize <= Integer.MAX_VALUE;
            case MULTIPLY_3 -> newSize / 3 == oldSize && newSize <= Integer.MAX_VALUE;
            case DIVIDE_2 -> newSize != 0 && newSize << 1 == oldSize;
            case DIVIDE_3 -> newSize != 0 && newSize * 3 == oldSize;
            default -> throw new RuntimeException("Unreasonable calls");
        };
    }
}