package com.circulation.random_complement.client.handler;

import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.common.network.WirelessPickBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InputHendler {

    public static InputHendler INSTANCE = new InputHendler();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static int tick = 0;

    private InputHendler(){

    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
        if (tick > 0){
            tick--;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInputEvent(final InputEvent.KeyInputEvent event) {
        if (!mc.player.isCreative() && tick == 0 && mc.gameSettings.keyBindPickBlock.isPressed()) {
            EntityPlayer player = mc.player;
            World world = player.world;
            RayTraceResult target = mc.objectMouseOver;

            ItemStack result = ItemStack.EMPTY;
            boolean isCreative = mc.player.isCreative();
            TileEntity te = null;

            if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
                IBlockState state = world.getBlockState(target.getBlockPos());

                if (state.getBlock().isAir(state, world, target.getBlockPos())) return;

                if (isCreative && GuiScreen.isCtrlKeyDown() && state.getBlock().hasTileEntity(state)) te = world.getTileEntity(target.getBlockPos());

                result = state.getBlock().getPickBlock(state, target, world, target.getBlockPos(), player);
            }

            if (result.isEmpty()) {
                return;
            }

            if (te != null) {
                Minecraft.getMinecraft().storeTEInStack(result, te);
            }

            if (player.isSneaking()){
                result.setCount(1);
            } else {
                result.setCount(result.getItem().getItemStackLimit(result));
            }

            int slot = player.inventory.getSlotFor(result);
            if (InventoryPlayer.isHotbar(slot)) {
                player.inventory.currentItem = slot;
            }

            RandomComplement.NET_CHANNEL.sendToServer(new WirelessPickBlock(result,player.inventory.currentItem));
            tick = 20;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInputEvent(final InputEvent.MouseInputEvent event) {
        if (!mc.player.isCreative() && tick == 0 && mc.gameSettings.keyBindPickBlock.isPressed()) {
            EntityPlayer player = mc.player;
            World world = player.world;
            RayTraceResult target = mc.objectMouseOver;

            ItemStack result = ItemStack.EMPTY;
            boolean isCreative = mc.player.isCreative();
            TileEntity te = null;

            if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
                IBlockState state = world.getBlockState(target.getBlockPos());

                if (state.getBlock().isAir(state, world, target.getBlockPos())) return;

                if (isCreative && GuiScreen.isCtrlKeyDown() && state.getBlock().hasTileEntity(state)) te = world.getTileEntity(target.getBlockPos());

                result = state.getBlock().getPickBlock(state, target, world, target.getBlockPos(), player);
            }

            if (result.isEmpty()) {
                return;
            }

            if (te != null) {
                Minecraft.getMinecraft().storeTEInStack(result, te);
            }

            if (player.isSneaking()){
                result.setCount(1);
            } else {
                result.setCount(result.getItem().getItemStackLimit(result));
            }

            int slot = player.inventory.getSlotFor(result);
            if (InventoryPlayer.isHotbar(slot)) {
                player.inventory.currentItem = slot;
            } else if (slot != -1){
                return;
            }

            if (slot == -1) {
                for (int i = 0; i < 9; i++) {
                    if (player.inventory.getStackInSlot(i).isEmpty()){
                        player.inventory.currentItem = i;
                        break;
                    }
                }
            }

            RandomComplement.NET_CHANNEL.sendToServer(new WirelessPickBlock(result,player.inventory.currentItem));
            tick = 20;
        }
    }

}
