package com.circulation.random_complement.client.handler;

import appeng.client.gui.implementations.GuiMEMonitorable;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.KeyBindings;
import com.circulation.random_complement.common.network.KeyBindingHandler;
import com.circulation.random_complement.common.network.WirelessPickBlock;
import com.circulation.random_complement.common.util.Function;
import com.glodblock.github.common.item.fake.FakeFluids;
import com.glodblock.github.integration.mek.FakeGases;
import mekanism.api.gas.GasStack;
import mezz.jei.gui.overlay.bookmarks.LeftAreaDispatcher;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.MouseHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class InputHandler {

    public static InputHandler INSTANCE = new InputHandler();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static int tick = 0;
    public static int counter = 0;
    private static int counter1 = 0;
    public static LeftAreaDispatcher leftAreaDispatcher;
    public static GuiScreen oldGui = null;
    public static Runnable delayMethod = null;

    private InputHandler(){

    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
        if (tick > 0){
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

            if (slot == -1 && !player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
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

            if (slot == -1 && !player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuiKeyboardEventPre(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (work(false)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuiMouseEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (work(true)){
            event.setCanceled(true);
        }
    }

    private boolean work(boolean isMouse){
        int eventKey;
        int m = 0;
        if (isMouse){
            m = Mouse.getEventButton();
            eventKey = m - 100;
        } else {
            eventKey = Keyboard.getEventKey();
        }
        for (KeyBindings kb : KeyBindings.values()) {
            var k = kb.getKeyBinding();
            if (k.isActiveAndMatches(eventKey)
                    && k.getKeyModifier().isActive(k.getKeyConflictContext())) {
                if (kb.needItem()) {
                    var ing = leftAreaDispatcher.getIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
                    if (ing == null) return false;
                    if (isMouse && !Mouse.isButtonDown(m)) {
                        return true;
                    }
                    ItemStack item = ItemStack.EMPTY;
                    if (ing.getValue() instanceof ItemStack i) {
                        item = i.copy();
                    } else if (Function.modLoaded("ae2fc")) {
                        item = ae2fcWork(ing);
                    }
                    if (item.isEmpty()) return false;
                    final var oldGui = Minecraft.getMinecraft().currentScreen;

                    RandomComplement.NET_CHANNEL.sendToServer(new KeyBindingHandler(kb.name(), item, Minecraft.getMinecraft().currentScreen instanceof GuiMEMonitorable));

                    if (kb == KeyBindings.StartCraft) {
                        //InputHandler.oldGui = oldGui;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Optional.Method(modid = "ae2fc")
    public ItemStack ae2fcWork(IClickedIngredient<?> ing){
        if (ing.getValue() instanceof FluidStack i) {
            var ii = FakeFluids.packFluid2Drops(i);
            if (ii != null){
                return ii;
            }
        } else if (Function.modLoaded("mekeng")){
            return mekengWork(ing);
        }
        return ItemStack.EMPTY;
    }

    @Optional.Method(modid = "mekeng")
    private ItemStack mekengWork(IClickedIngredient<?> ing){
        if (ing.getValue() instanceof GasStack i) {
            var ii = FakeGases.packGas2Drops(i);
            if (ii != null){
                return ii;
            }
        }
        return ItemStack.EMPTY;
    }
}
