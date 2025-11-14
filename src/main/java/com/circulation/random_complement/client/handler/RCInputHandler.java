package com.circulation.random_complement.client.handler;

import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.common.network.WirelessPickBlock;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class RCInputHandler {

    @Getter
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static RCInputHandler INSTANCE = new RCInputHandler();
    private static int tick = 0;
    @Getter
    private static int counter = 0;
    private static int counter1 = 0;
    @Setter
    @Getter
    private static GuiScreen oldGui = null;
    @Setter
    @Getter
    private static Runnable delayMethod = null;
    @Getter
    private static ScaledResolution resolution;

    private RCInputHandler() {

    }

    public static int getMouseX() {
        int i = resolution.getScaledWidth();
        return Mouse.getX() * i / mc.displayWidth;
    }

    public static int getMouseY() {
        int j = resolution.getScaledHeight();
        return j - Mouse.getY() * j / mc.displayHeight - 1;
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        resolution = new ScaledResolution(mc);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        if (tick > 0) {
            tick--;
        }
        counter = (counter + ((++counter1 & 1) == 0 ? 1 : 0)) % 14;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInputEvent(final InputEvent.KeyInputEvent event) {
        if (!mc.player.isCreative() && tick == 0 && mc.gameSettings.keyBindPickBlock.isPressed()) {
            ForgeHooks.onPickBlock(mc.objectMouseOver, mc.player, mc.world);

            EntityPlayer player = mc.player;
            World world = player.world;
            RayTraceResult target = mc.objectMouseOver;

            ItemStack result = ItemStack.EMPTY;
            boolean isCreative = mc.player.isCreative();
            TileEntity te = null;

            if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
                IBlockState state = world.getBlockState(target.getBlockPos());

                if (state.getBlock().isAir(state, world, target.getBlockPos())) return;

                if (isCreative && GuiScreen.isCtrlKeyDown() && state.getBlock().hasTileEntity(state))
                    te = world.getTileEntity(target.getBlockPos());

                result = state.getBlock().getPickBlock(state, target, world, target.getBlockPos(), player).copy();
            }

            if (result.isEmpty()) {
                return;
            }

            if (te != null) {
                Minecraft.getMinecraft().storeTEInStack(result, te);
            }

            if (player.isSneaking()) {
                result.setCount(1);
            } else {
                result.setCount(result.getItem().getItemStackLimit(result));
            }

            int slot = player.inventory.getSlotFor(result);
            if (InventoryPlayer.isHotbar(slot)) {
                player.inventory.currentItem = slot;
            } else if (slot != -1) {
                return;
            }

            if (slot == -1 && !player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
                for (int i = 0; i < 9; i++) {
                    if (player.inventory.getStackInSlot(i).isEmpty()) {
                        player.inventory.currentItem = i;
                        break;
                    }
                }
            }

            RandomComplement.NET_CHANNEL.sendToServer(new WirelessPickBlock(result, player.inventory.currentItem));
            tick = 20;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInputEvent(final InputEvent.MouseInputEvent event) {
        if (!mc.player.isCreative() && tick == 0 && mc.gameSettings.keyBindPickBlock.isPressed()) {
            ForgeHooks.onPickBlock(mc.objectMouseOver, mc.player, mc.world);

            EntityPlayer player = mc.player;
            World world = player.world;
            RayTraceResult target = mc.objectMouseOver;

            ItemStack result = ItemStack.EMPTY;
            boolean isCreative = mc.player.isCreative();
            TileEntity te = null;

            if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
                IBlockState state = world.getBlockState(target.getBlockPos());

                if (state.getBlock().isAir(state, world, target.getBlockPos())) return;

                if (isCreative && GuiScreen.isCtrlKeyDown() && state.getBlock().hasTileEntity(state))
                    te = world.getTileEntity(target.getBlockPos());

                result = state.getBlock().getPickBlock(state, target, world, target.getBlockPos(), player).copy();
            }

            if (result.isEmpty()) {
                return;
            }

            if (te != null) {
                Minecraft.getMinecraft().storeTEInStack(result, te);
            }

            if (player.isSneaking()) {
                result.setCount(1);
            } else {
                result.setCount(result.getItem().getItemStackLimit(result));
            }

            int slot = player.inventory.getSlotFor(result);
            if (InventoryPlayer.isHotbar(slot)) {
                player.inventory.currentItem = slot;
            } else if (slot != -1) {
                return;
            }

            if (slot == -1 && !player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
                for (int i = 0; i < 9; i++) {
                    if (player.inventory.getStackInSlot(i).isEmpty()) {
                        player.inventory.currentItem = i;
                        break;
                    }
                }
            }

            RandomComplement.NET_CHANNEL.sendToServer(new WirelessPickBlock(result, player.inventory.currentItem));
            tick = 20;
        }
    }
}