package com.circulation.random_complement.common.network;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.implementations.ContainerCraftAmount;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.core.localization.PlayerMessages;
import appeng.core.sync.GuiBridge;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.me.helpers.PlayerSource;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import baubles.api.BaublesApi;
import com.circulation.random_complement.common.handler.MEHandler;
import com.circulation.random_complement.mixin.ae2.container.AccessorContainerMEMonitorable;
import com.github.bsideup.jabel.Desugar;
import com.glodblock.github.common.item.ItemWirelessFluidPatternTerminal;
import com.glodblock.github.common.part.PartExtendedFluidPatternTerminal;
import com.glodblock.github.common.part.PartFluidPatternTerminal;
import com.glodblock.github.inventory.GuiType;
import com.glodblock.github.inventory.InventoryHandler;
import com.glodblock.github.util.Ae2Reflect;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Iterator;
import java.util.Optional;

public class KeyBindingHandle implements IMessage {

    ItemStack stack = ItemStack.EMPTY;
    String key;
    boolean isAE = false;

    public KeyBindingHandle(){

    }

    public KeyBindingHandle(String key){
        this.key = key;
    }

    public KeyBindingHandle(String key, ItemStack item, boolean isAE){
        this.key = key;
        this.stack = item;
        this.isAE = isAE;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.stack = ByteBufUtils.readItemStack(buf);
        this.key = ByteBufUtils.readUTF8String(buf);
        this.isAE = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.stack);
        ByteBufUtils.writeUTF8String(buf,this.key);
        buf.writeBoolean(this.isAE);
    }

    public static class Handler implements IMessageHandler<KeyBindingHandle, IMessage> {

        @Override
        public IMessage onMessage(KeyBindingHandle message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            var container = player.openContainer;
            var item = message.stack;
            switch (message.key){
                case "RetrieveItem" -> retrieveItem(player,container,item,message.isAE);
                case "StartCraft" -> player.getServer().addScheduledTask(() -> startCraft(player,container,item,message.isAE));
            }
            return null;
        }

        private void retrieveItem(EntityPlayerMP player, Container container, ItemStack item, boolean isAE){
            long targetCount = item.getMaxStackSize();
            if (!isAE) {
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    ItemStack ii = player.inventory.getStackInSlot(i);
                    WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(ii,player,i,0,Integer.MIN_VALUE);

                    if (obj == null){
                        continue;
                    }

                    if (!obj.rangeCheck()) {
                        player.sendMessage(PlayerMessages.OutOfRange.get());
                    } else {
                        IGridNode gridNode = obj.getActionableNode();
                        if (gridNode == null) {
                            player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                            continue;
                        }
                        targetCount = wirelessRetrieve(player, item, gridNode, targetCount, obj);
                        if (targetCount <= 0){
                            return;
                        }
                    }
                }
                if (Loader.isModLoaded("baubles")) {
                    readBaublesR(player,item,targetCount);
                }
            } else if (container instanceof ContainerMEMonitorable c) {
                IGridNode gridNode = c.getNetworkNode();
                if (gridNode == null) {
                    player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                    return;
                }
                IGrid grid = gridNode.getGrid();
                if (securityCheck(player, grid, SecurityPermissions.EXTRACT)) {
                    IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                    var iItemStorageChannel = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                    var host = c.getTarget();
                    if (host instanceof IActionHost h) {
                        var aeItemO = Optional.ofNullable(iItemStorageChannel.extractItems(AEItemStack.fromItemStack(item).setStackSize(targetCount), Actionable.SIMULATE, new PlayerSource(player, h)));

                        if (aeItemO.isPresent()) {
                            var aeItem = aeItemO.get();
                            var aeitem = iItemStorageChannel.extractItems(AEItemStack.fromItemStack(item).setStackSize(aeItem.getStackSize()), Actionable.MODULATE, new PlayerSource(player, h));
                            player.inventory.placeItemBackInInventory(player.world, aeitem.createItemStack());
                        }
                    }
                }
            }
        }

        private long wirelessRetrieve(EntityPlayerMP player, ItemStack exItem, IGridNode gridNode, long targetCount, WirelessTerminalGuiObject obj) {
            IGrid grid = gridNode.getGrid();
            if (securityCheck(player, grid, SecurityPermissions.EXTRACT)) {
                IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                var iItemStorageChannel = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                var aeItemO = Optional.ofNullable(iItemStorageChannel.extractItems(AEItemStack.fromItemStack(exItem).setStackSize(targetCount), Actionable.SIMULATE, new PlayerSource(player, obj)));

                if (aeItemO.isPresent()) {
                    var aeItem = aeItemO.get();
                    var aeitem = iItemStorageChannel.extractItems(AEItemStack.fromItemStack(exItem).setStackSize(aeItem.getStackSize()), Actionable.MODULATE, new PlayerSource(player, obj));

                    targetCount -= aeitem.getStackSize();

                    player.inventory.placeItemBackInInventory(player.world, aeitem.createItemStack());
                }
            }
            return targetCount;
        }

        private void startCraft(EntityPlayerMP player, Container container, ItemStack item, boolean isAE){
            item.setCount(1);
            if (!isAE) {
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    ItemStack ii = player.inventory.getStackInSlot(i);
                    WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(ii,player,i,0,Integer.MIN_VALUE);

                    if (obj == null){
                        continue;
                    }

                    if (!obj.rangeCheck()) {
                        player.sendMessage(PlayerMessages.OutOfRange.get());
                    } else {
                        IGridNode gridNode = obj.getActionableNode();
                        if (gridNode == null) {
                            player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                            continue;
                        }
                        openWirelessCraft(ii,player, item, gridNode, i,false);
                        return;
                    }
                }
                if (Loader.isModLoaded("baubles")) {
                    readBaublesS(player,item);
                }
            } else if (container instanceof ContainerMEMonitorable c) {
                IGridNode gridNode = c.getNetworkNode();
                if (gridNode == null) {
                    player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                    return;
                }
                IGrid grid = gridNode.getGrid();
                if (securityCheck(player, grid, SecurityPermissions.CRAFT)) {
                    Iterator<IAEItemStack> iterator = ((AccessorContainerMEMonitorable)c).getMonitor().getStorageList().iterator();
                    boolean isCraftable = false;
                    IAEItemStack aeItem = null;
                    while (iterator.hasNext()){
                        aeItem = iterator.next();
                        if (aeItem.isCraftable()){
                            if (aeItem.equals(item)){
                                aeItem = aeItem.copy().setStackSize(1);
                                isCraftable = true;
                                break;
                            }
                        }
                    }
                    if (!isCraftable){
                        player.sendMessage(new TextComponentTranslation("text.rc.craft"));
                        return;
                    }

                    var host = c.getTarget();
                    if (host instanceof IActionHost) {
                        if (Loader.isModLoaded("ae2fc")) ae2fcCraft(host,player,c);
                        else Platform.openGUI(player,c.getOpenContext().getTile(), c.getOpenContext().getSide(), GuiBridge.GUI_CRAFTING_AMOUNT);

                        if (player.openContainer instanceof ContainerCraftAmount cca){
                            cca.getCraftingItem().putStack(aeItem.asItemStackRepresentation());
                            cca.setItemToCraft(aeItem);
                            cca.detectAndSendChanges();
                        }
                    }
                }
            }
        }

        private void openWirelessCraft(ItemStack terminal, EntityPlayerMP player, ItemStack exItem, IGridNode gridNode, int i, boolean isBauble) {
            IGrid grid = gridNode.getGrid();
            if (securityCheck(player, grid, SecurityPermissions.CRAFT)) {
                IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                var iItemStorageChannel = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

                Iterator<IAEItemStack> iterator = iItemStorageChannel.getStorageList().iterator();
                boolean isCraftable = false;
                IAEItemStack aeItem = null;
                while (iterator.hasNext()){
                    aeItem = iterator.next();
                    if (aeItem.isCraftable()){
                        if (aeItem.equals(exItem)){
                            aeItem = aeItem.copy().setStackSize(1);
                            isCraftable = true;
                            break;
                        }
                    }
                }

                if (!isCraftable){
                    player.sendMessage(new TextComponentTranslation("text.rc.craft"));
                    return;
                }

                if (Loader.isModLoaded("ae2fc")) ae2fcCraft(terminal,player,new Terminal(i,isBauble));
                else Platform.openGUI(player, i,GuiBridge.GUI_CRAFTING_AMOUNT,isBauble);

                if (player.openContainer instanceof ContainerCraftAmount cca){
                    cca.getCraftingItem().putStack(aeItem.asItemStackRepresentation());
                    cca.setItemToCraft(aeItem);
                    cca.detectAndSendChanges();
                }
            }
        }

        @Desugar
        public record Terminal(int i, boolean isBauble) {}

        @Method(modid = "ae2fc")
        private void ae2fcCraft(Object host, EntityPlayerMP player, Object a){
            if (host instanceof IActionHost) {
                if (a instanceof ContainerMEMonitorable c) {
                    if (host instanceof PartExtendedFluidPatternTerminal
                            || host instanceof PartFluidPatternTerminal
                            || (host instanceof WirelessTerminalGuiObject w &&
                                 (w.getItemStack().getItem() instanceof ItemWirelessFluidPatternTerminal || (w.getItemStack().hasTagCompound() && w.getItemStack().getTagCompound().getByte("mode") == 4)))) {
                        var context = c.getOpenContext();
                        player.getServerWorld().addScheduledTask(() -> InventoryHandler.openGui(player, Ae2Reflect.getContextWorld(context), new BlockPos(Ae2Reflect.getContextX(context), Ae2Reflect.getContextY(context), Ae2Reflect.getContextZ(context)), context.getSide().getFacing(), GuiType.FLUID_CRAFT_AMOUNT));
                        return;
                    }
                    Platform.openGUI(player,c.getOpenContext().getTile(), c.getOpenContext().getSide(), GuiBridge.GUI_CRAFTING_AMOUNT);
                }
            } else if (host instanceof ItemStack terminal) {
                if (a instanceof Terminal t) {
                   if (terminal.getItem() instanceof ItemWirelessFluidPatternTerminal || (terminal.hasTagCompound() && terminal.getTagCompound().getByte("mode") == 4)) {
                       player.getServerWorld().addScheduledTask(() -> InventoryHandler.openGui(player, player.world, new BlockPos(t.i(), t.isBauble() ? 1 : 0, Integer.MIN_VALUE), EnumFacing.DOWN, GuiType.FLUID_CRAFT_AMOUNT));
                       return;
                   }
                    Platform.openGUI(player, t.i(), GuiBridge.GUI_CRAFTING_AMOUNT, t.isBauble());
                }
            }
        }

        @Method(modid = "baubles")
        private void readBaublesS(EntityPlayerMP player,ItemStack exitem) {
            for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
                ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
                WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(item,player,i,1,Integer.MIN_VALUE);

                if (obj == null){
                    continue;
                }

                if (!obj.rangeCheck()) {
                    player.sendMessage(PlayerMessages.OutOfRange.get());
                } else {
                    IGridNode gridNode = obj.getActionableNode();
                    if (gridNode == null) {
                        player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                        continue;
                    }
                    openWirelessCraft(item,player,exitem,gridNode,i,true);
                    return;
                }
            }
        }

        @Method(modid = "baubles")
        private void readBaublesR(EntityPlayerMP player,ItemStack exitem,long targetCount) {
            for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
                ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
                WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(item,player,i,1,Integer.MIN_VALUE);

                if (obj == null){
                    continue;
                }

                if (!obj.rangeCheck()) {
                    player.sendMessage(PlayerMessages.OutOfRange.get());
                } else {
                    IGridNode gridNode = obj.getActionableNode();
                    if (gridNode == null) {
                        player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                        continue;
                    }
                    targetCount = wirelessRetrieve(player, exitem, gridNode, targetCount, obj);
                    if (targetCount <= 0){
                        return;
                    }
                }
            }
        }

        private boolean securityCheck(final EntityPlayer player, IGrid gridNode, final SecurityPermissions requiredPermission) {
            final ISecurityGrid sg = gridNode.getCache(ISecurityGrid.class);
            return sg.hasPermission(player, requiredPermission);
        }

    }
}
