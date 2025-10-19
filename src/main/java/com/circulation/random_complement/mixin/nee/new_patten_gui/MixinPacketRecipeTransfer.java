package com.circulation.random_complement.mixin.nee.new_patten_gui;

import appeng.api.networking.IGridNode;
import appeng.container.implementations.ContainerPatternTerm;
import appeng.util.helpers.ItemHandlerUtil;
import appeng.util.inv.WrapperInvItemHandler;
import com.github.vfyjxf.nee.network.packet.PacketRecipeTransfer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PacketRecipeTransfer.Handler.class)
public abstract class MixinPacketRecipeTransfer {

    @Shadow(remap = false)
    protected abstract void setCraftingRecipe(ContainerPatternTerm container, boolean craftingMode);

    /**
     * @author circulation
     * @reason 防止出现数组越界
     */
    @Overwrite(remap = false)
    public IMessage onMessage(PacketRecipeTransfer message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        Container container = player.openContainer;
        player.getServerWorld().addScheduledTask(() -> {
            if (container instanceof ContainerPatternTerm cct) {
                IItemHandler craftMatrix = cct.getInventoryByName("crafting");

                this.setCraftingRecipe(cct, message.getCraftingMode());
                ItemStack[] recipeInputs = new ItemStack[craftMatrix.getSlots()];
                ItemStack[] recipeOutputs = null;

                for (int i = 0; i < recipeInputs.length; ++i) {
                    NBTTagCompound currentStack = message.getInput().getCompoundTag("#" + i);
                    recipeInputs[i] = currentStack.isEmpty() ? ItemStack.EMPTY : new ItemStack(currentStack);
                }

                if (!message.getOutput().isEmpty()) {
                    recipeOutputs = new ItemStack[cct.getInventoryByName("output").getSlots()];

                    for (int i = 0; i < recipeOutputs.length; ++i) {
                        NBTTagCompound currentStack = message.getOutput().getCompoundTag("O" + i);
                        recipeOutputs[i] = currentStack.isEmpty() ? ItemStack.EMPTY : new ItemStack(currentStack);
                    }
                }

                IGridNode node = cct.getNetworkNode();
                if (node == null) {
                    return;
                }

                if (message.getInput() != null) {
                    for (int i = 0; i < craftMatrix.getSlots(); ++i) {
                        ItemStack currentItem = ItemStack.EMPTY;
                        if (recipeInputs[i] != null) {
                            currentItem = recipeInputs[i].copy();
                        }

                        ItemHandlerUtil.setStackInSlot(craftMatrix, i, currentItem);
                    }

                    if (recipeOutputs != null && !message.getCraftingMode()) {
                        IItemHandler outputMatrix = cct.getInventoryByName("output");

                        for (int i = 0; i < outputMatrix.getSlots(); ++i) {
                            ItemStack currentItem = ItemStack.EMPTY;
                            if (recipeOutputs[i] != null) {
                                currentItem = recipeOutputs[i].copy();
                            }

                            ItemHandlerUtil.setStackInSlot(outputMatrix, i, currentItem);
                        }
                    }

                    container.onCraftMatrixChanged(new WrapperInvItemHandler(craftMatrix));
                }
            }

        });
        return null;
    }
}