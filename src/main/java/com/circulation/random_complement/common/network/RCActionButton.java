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
import net.minecraftforge.common.util.Constants;
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
                            val in = pNbt.getTagList("in", Constants.NBT.TAG_COMPOUND);
                            boolean success = true;
                            List<NBTBase> list = new ObjectArrayList<>(in.tagList);
                            list.addAll(pNbt.getTagList("out", Constants.NBT.TAG_COMPOUND).tagList);
                            if (pNbt.hasKey("Inputs")) {
                                list.addAll(pNbt.getTagList("Inputs", Constants.NBT.TAG_COMPOUND).tagList);
                                list.addAll(pNbt.getTagList("Outputs", Constants.NBT.TAG_COMPOUND).tagList);
                            }
                            for (NBTBase item : list) {
                                if (!(item instanceof NBTTagCompound nbt) || !nbt.hasKey("Count")) continue;
                                int oldSize = nbt.hasKey("stackSize") ? nbt.getInteger("stackSize") : nbt.getInteger("Count");
                                if (!r$correctQuantity(oldSize, action)) {
                                    success = false;
                                    break;
                                }
                                if (success) {
                                    int newSize = r$quantityProcessing(oldSize, action);
                                    nbt.setInteger("Count", newSize);
                                    if (nbt.hasKey("Cnt")) {
                                        oldSize = nbt.getInteger("Cnt");
                                        if (!r$correctQuantity(oldSize, action)) {
                                            success = false;
                                            break;
                                        }
                                        if (success)
                                            nbt.setInteger("Cnt", r$quantityProcessing(oldSize, action));
                                    }
                                    if (newSize > 127) nbt.setInteger("stackSize", newSize);
                                    else nbt.removeTag("stackSize");
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
    private int r$quantityProcessing(int size, Action action) {
        return switch (action) {
            case MULTIPLY_2 -> size * 2;
            case MULTIPLY_3 -> size * 3;
            case DIVIDE_2 -> size / 2;
            case DIVIDE_3 -> size / 3;
            default -> throw new RuntimeException("Unreasonable calls");
        };
    }

    @Unique
    private boolean r$correctQuantity(int oldSize, Action action) {
        return switch (action) {
            case MULTIPLY_2 -> oldSize <= (Integer.MAX_VALUE / 2);
            case MULTIPLY_3 -> oldSize <= (Integer.MAX_VALUE / 3);
            case DIVIDE_2 -> oldSize % 2 == 0;
            case DIVIDE_3 -> oldSize % 3 == 0;
            default -> throw new RuntimeException("Unreasonable calls");
        };
    }
}