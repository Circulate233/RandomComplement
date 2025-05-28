package com.circulation.random_complement.common.network;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
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
import appeng.tile.misc.TileSecurityStation;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import baubles.api.BaublesApi;
import com.circulation.random_complement.mixin.ae2.container.AccessorContainerMEMonitorable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class KeyBindingHandle implements IMessage {

    private ItemStack stack = ItemStack.EMPTY;
    private String key = "";
    private boolean isAE = false;

    public KeyBindingHandle(){

    }

    public KeyBindingHandle(String key){
        this.key = key;
    }

    public KeyBindingHandle(String key,ItemStack item,boolean isAE){
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

        private static final Map<UUID,Long> map = new ConcurrentHashMap<>();

        @Override
        public IMessage onMessage(KeyBindingHandle message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            var container = player.openContainer;
            var item = message.stack;
            switch (message.key){
                case "RetrieveItem":
                    long targetCount = item.getMaxStackSize();
                    if (!message.isAE) {
                        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                            ItemStack ii = player.inventory.getStackInSlot(i);
                            if (ii.getItem() instanceof IWirelessTermHandler wt && wt.canHandle(ii)) {
                                int handItemConnt = 0;
                                var pos = new BlockPos(i,1,Integer.MIN_VALUE);

                                IWirelessTermHandler handler = AEApi.instance().registries().wireless().getWirelessTerminalHandler(ii);
                                String unparsedKey = handler.getEncryptionKey(ii);
                                if (unparsedKey.isEmpty()) {
                                    player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                                    continue;
                                }
                                long parsedKey = Long.parseLong(unparsedKey);
                                ILocatable securityStation = AEApi.instance().registries().locatable().getLocatableBy(parsedKey);
                                if (securityStation instanceof TileSecurityStation t) {
                                    if (!handler.hasPower(player, 1000F, ii)) {
                                        player.sendMessage(PlayerMessages.DeviceNotPowered.get());
                                        continue;
                                    }
                                    WirelessTerminalGuiObject obj = new WirelessTerminalGuiObject(handler, ii, player, player.world, pos.getX(), pos.getY(), pos.getZ());

                                    if (!obj.rangeCheck()) {
                                        player.sendMessage(PlayerMessages.OutOfRange.get());
                                    } else {
                                        IGridNode gridNode = obj.getActionableNode();
                                        if (gridNode == null) {
                                            player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                                            continue;
                                        }
                                        IGrid grid = gridNode.getGrid();
                                        if (securityCheck(player, grid, SecurityPermissions.EXTRACT)) {
                                            IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                                            var iItemStorageChannel = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                                            var aeItem = iItemStorageChannel.extractItems(AEItemStack.fromItemStack(item).setStackSize(targetCount), Actionable.SIMULATE, new PlayerSource(player, t));
                                            if (aeItem != null && aeItem.getStackSize() > 0) {
                                                var aeitem = iItemStorageChannel.extractItems(AEItemStack.fromItemStack(item).setStackSize(aeItem.getStackSize()), Actionable.MODULATE, new PlayerSource(player, t));

                                                targetCount -= aeitem.getStackSize();

                                                player.inventory.placeItemBackInInventory(player.world, aeitem.createItemStack());
                                            }
                                        }
                                        if (targetCount <= 0){
                                            return null;
                                        }
                                    }
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
                            return null;
                        }
                        IGrid grid = gridNode.getGrid();
                        if (securityCheck(player, grid, SecurityPermissions.EXTRACT)) {
                            IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                            var iItemStorageChannel = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                            var host = c.getTarget();
                            if (host instanceof IActionHost h) {
                                var aeItem = iItemStorageChannel.extractItems(AEItemStack.fromItemStack(item).setStackSize(targetCount), Actionable.SIMULATE, new PlayerSource(player, h));
                                if (aeItem != null && aeItem.getStackSize() > 0) {
                                    var aeitem = iItemStorageChannel.extractItems(AEItemStack.fromItemStack(item).setStackSize(aeItem.getStackSize()), Actionable.MODULATE, new PlayerSource(player, h));
                                    player.inventory.placeItemBackInInventory(player.world, aeitem.createItemStack());
                                }
                            }
                        }
                    }
                    break;
                case "StartCraft":
                    UUID playUUID = player.getUniqueID();
                    long worldTime = Instant.now().getEpochSecond();
                    if (map.containsKey(playUUID)){
                        if (map.get(playUUID) < worldTime){
                            map.put(playUUID,worldTime);
                        } else {
                            player.sendMessage(new TextComponentTranslation("text.rc.warn"));
                            return null;
                        }
                    } else {
                        map.put(playUUID,worldTime);
                    }
                    item.setCount(1);
                    if (!message.isAE) {
                        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                            ItemStack ii = player.inventory.getStackInSlot(i);
                            if (ii.getItem() instanceof IWirelessTermHandler wt && wt.canHandle(ii)) {
                                int handItemConnt = 0;
                                var pos = new BlockPos(i,1,Integer.MIN_VALUE);

                                IWirelessTermHandler handler = AEApi.instance().registries().wireless().getWirelessTerminalHandler(ii);
                                String unparsedKey = handler.getEncryptionKey(ii);
                                if (unparsedKey.isEmpty()) {
                                    player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                                    return null;
                                }
                                long parsedKey = Long.parseLong(unparsedKey);
                                ILocatable securityStation = AEApi.instance().registries().locatable().getLocatableBy(parsedKey);
                                if (securityStation instanceof TileSecurityStation t) {
                                    if (!handler.hasPower(player, 1000F, ii)) {
                                        player.sendMessage(PlayerMessages.DeviceNotPowered.get());
                                        return null;
                                    }

                                    WirelessTerminalGuiObject obj = new WirelessTerminalGuiObject(handler, ii, player, player.world, pos.getX(), pos.getY(), pos.getZ());

                                    if (!obj.rangeCheck()) {
                                        player.sendMessage(PlayerMessages.OutOfRange.get());
                                    } else {
                                        IGridNode gridNode = obj.getActionableNode();
                                        if (gridNode == null) {
                                            player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                                            return null;
                                        }
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
                                                    if (aeItem.equals(item)){
                                                        aeItem = aeItem.copy().setStackSize(1);
                                                        isCraftable = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (!isCraftable){
                                                player.sendMessage(new TextComponentTranslation("text.rc.craft"));
                                                return null;
                                            }

                                            Platform.openGUI(player,i,GuiBridge.GUI_CRAFTING_AMOUNT,false);

                                            if (player.openContainer instanceof ContainerCraftAmount cca){
                                                cca.getCraftingItem().putStack(aeItem.asItemStackRepresentation());
                                                cca.setItemToCraft(aeItem);
                                                cca.detectAndSendChanges();
                                            }
                                        }
                                        return null;
                                    }
                                }
                            }
                        }
                        if (Loader.isModLoaded("baubles")) {
                            readBaublesS(player,item);
                        }
                    } else if (container instanceof ContainerMEMonitorable c) {
                        IGridNode gridNode = c.getNetworkNode();
                        if (gridNode == null) {
                            player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                            return null;
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
                                return null;
                            }

                            var host = c.getTarget();
                            if (host instanceof IActionHost h) {
                                Platform.openGUI(player,c.getOpenContext().getTile(), c.getOpenContext().getSide(), GuiBridge.GUI_CRAFTING_AMOUNT);

                                if (player.openContainer instanceof ContainerCraftAmount cca){
                                    cca.getCraftingItem().putStack(aeItem.asItemStackRepresentation());
                                    cca.setItemToCraft(aeItem);
                                    cca.detectAndSendChanges();
                                }
                            }
                        }
                    }
                    break;
            }
            return null;
        }

        @Optional.Method(modid = "baubles")
        public void readBaublesS(EntityPlayer player,ItemStack exitem) {
            for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
                ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
                if (item.getItem() instanceof IWirelessTermHandler wt && wt.canHandle(item)) {
                    int handItemConnt = 0;
                    var pos = new BlockPos(i,1,Integer.MIN_VALUE);

                    IWirelessTermHandler handler = AEApi.instance().registries().wireless().getWirelessTerminalHandler(item);
                    String unparsedKey = handler.getEncryptionKey(item);
                    if (unparsedKey.isEmpty()) {
                        player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                        return;
                    }
                    long parsedKey = Long.parseLong(unparsedKey);
                    ILocatable securityStation = AEApi.instance().registries().locatable().getLocatableBy(parsedKey);
                    if (securityStation instanceof TileSecurityStation t) {
                        if (!handler.hasPower(player, 1000F, item)) {
                            player.sendMessage(PlayerMessages.DeviceNotPowered.get());
                            return;
                        }
                        WirelessTerminalGuiObject obj = new WirelessTerminalGuiObject(handler, item, player, player.world, pos.getX(), pos.getY(), pos.getZ());

                        if (!obj.rangeCheck()) {
                            player.sendMessage(PlayerMessages.OutOfRange.get());
                        } else {
                            IGridNode gridNode = obj.getActionableNode();
                            if (gridNode == null) {
                                player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                                return;
                            }
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

                                Platform.openGUI(player,i,GuiBridge.GUI_CRAFTING_AMOUNT,true);

                                if (player.openContainer instanceof ContainerCraftAmount cca){
                                    cca.getCraftingItem().putStack(aeItem.asItemStackRepresentation());
                                    cca.setItemToCraft(aeItem);
                                    cca.detectAndSendChanges();
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }

        @Optional.Method(modid = "baubles")
        public void readBaublesR(EntityPlayer player,ItemStack exitem,long targetCount) {
            for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
                ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
                if (item.getItem() instanceof IWirelessTermHandler wt && wt.canHandle(item)) {
                    int handItemConnt = 0;
                    var pos = new BlockPos(i,1,Integer.MIN_VALUE);

                    IWirelessTermHandler handler = AEApi.instance().registries().wireless().getWirelessTerminalHandler(item);
                    String unparsedKey = handler.getEncryptionKey(item);
                    if (unparsedKey.isEmpty()) {
                        player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                        continue;
                    }
                    long parsedKey = Long.parseLong(unparsedKey);
                    ILocatable securityStation = AEApi.instance().registries().locatable().getLocatableBy(parsedKey);
                    if (securityStation instanceof TileSecurityStation t) {
                        if (!handler.hasPower(player, 1000F, item)) {
                            player.sendMessage(PlayerMessages.DeviceNotPowered.get());
                            continue;
                        }
                        WirelessTerminalGuiObject obj = new WirelessTerminalGuiObject(handler, item, player, player.world, pos.getX(), pos.getY(), pos.getZ());

                        if (!obj.rangeCheck()) {
                            player.sendMessage(PlayerMessages.OutOfRange.get());
                        } else {
                            IGridNode gridNode = obj.getActionableNode();
                            if (gridNode == null) {
                                player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                                continue;
                            }
                            IGrid grid = gridNode.getGrid();
                            if (securityCheck(player, grid, SecurityPermissions.EXTRACT)) {
                                IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                                var iItemStorageChannel = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                                var aeItem = iItemStorageChannel.extractItems(AEItemStack.fromItemStack(exitem).setStackSize(targetCount), Actionable.SIMULATE, new PlayerSource(player, t));
                                if (aeItem != null && aeItem.getStackSize() > 0) {
                                    var aeitem = iItemStorageChannel.extractItems(AEItemStack.fromItemStack(exitem).setStackSize(aeItem.getStackSize()), Actionable.MODULATE, new PlayerSource(player, t));

                                    targetCount -= aeitem.getStackSize();

                                    player.inventory.placeItemBackInInventory(player.world, aeitem.createItemStack());
                                }
                            }
                            if (targetCount <= 0){
                                return;
                            }
                        }
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
