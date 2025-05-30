package com.circulation.random_complement.common.network;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.me.helpers.PlayerSource;
import appeng.tile.misc.TileSecurityStation;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import baubles.api.BaublesApi;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WirelessPickBlock implements IMessage {

    private int slot;
    private ItemStack stack = ItemStack.EMPTY;

    public WirelessPickBlock(){

    }

    public WirelessPickBlock(ItemStack stack,int slot) {
        this.stack = stack;
        this.slot = slot;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.stack = ByteBufUtils.readItemStack(buf);
        this.slot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.stack);
        buf.writeInt(this.slot);
    }

    public static class Handler implements IMessageHandler<WirelessPickBlock, IMessage> {

        private static final Map<UUID,Long> map = new ConcurrentHashMap<>();

        @Override
        public IMessage onMessage(WirelessPickBlock message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            UUID playUUID = player.getUniqueID();
            Long worldTime = Instant.now().getEpochSecond();
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
            ItemStack handItem = player.inventory.getStackInSlot(message.slot);
            ItemStack needItem = message.stack.copy();
            if (!handItem.isEmpty()) {
                if (handItem.getCount() >= handItem.getItem().getItemStackLimit(handItem)) return null;
                else needItem.setCount(handItem.getItem().getItemStackLimit(handItem) - handItem.getCount());
            }

            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack item = player.inventory.getStackInSlot(i);
                if (item.getItem() instanceof IWirelessTermHandler wt && wt.canHandle(item)) {
                    if (work(item, player,needItem,message.slot,i,0,Integer.MIN_VALUE)) {
                        return null;
                    }
                }
            }

            if (Loader.isModLoaded("baubles")) {
                readBaubles(player,needItem, message.slot);
            }

            return null;
        }

        @Optional.Method(modid = "baubles")
        public void readBaubles(EntityPlayer player,ItemStack exitem,int slot) {
            for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
                ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
                if (item.getItem() instanceof IWirelessTermHandler wt && wt.canHandle(item)) {
                    if (work(item, player, exitem, slot, i, 1, Integer.MIN_VALUE)) {
                        return;
                    }
                }
            }
        }

        private boolean work(ItemStack item, EntityPlayer player, ItemStack exItem, int slot, int x,int y,int z) {
            if (Platform.isClient()) return false;
            int handItemConnt = 0;
            if (!player.inventory.getStackInSlot(slot).isEmpty()){
                ItemStack vitem = player.inventory.getStackInSlot(slot);
                if (exItem.getItem() != vitem.getItem() || exItem.getItemDamage() != vitem.getItemDamage() || exItem.getTagCompound() != vitem.getTagCompound())return false;
                handItemConnt = player.inventory.getStackInSlot(slot).getCount();
            }

            IWirelessTermHandler handler = AEApi.instance().registries().wireless().getWirelessTerminalHandler(item);
            String unparsedKey = handler.getEncryptionKey(item);
            if (unparsedKey.isEmpty()) {
                player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                return false;
            }
            long parsedKey = Long.parseLong(unparsedKey);
            ILocatable securityStation = AEApi.instance().registries().locatable().getLocatableBy(parsedKey);
            if (securityStation instanceof TileSecurityStation t) {
                if (!handler.hasPower(player, 1000F, item)) {
                    player.sendMessage(PlayerMessages.DeviceNotPowered.get());
                    return false;
                }
                WirelessTerminalGuiObject obj = new WirelessTerminalGuiObject(handler,item,player, player.world, x,y,z);

                if (!obj.rangeCheck()) {
                    player.sendMessage(PlayerMessages.OutOfRange.get());
                } else {
                    IGridNode gridNode = obj.getActionableNode();
                    if (gridNode == null)return false;
                    IGrid grid = gridNode.getGrid();
                    if (securityCheck(player, grid, SecurityPermissions.EXTRACT)) {
                        IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                        var iItemStorageChannel = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                        var aeItem = iItemStorageChannel.extractItems(AEItemStack.fromItemStack(exItem).setStackSize(exItem.getCount()), Actionable.SIMULATE, new PlayerSource(player, t));
                        if (aeItem != null && aeItem.getStackSize() > 0) {
                            var aeitem = iItemStorageChannel.extractItems(AEItemStack.fromItemStack(exItem).setStackSize(aeItem.getStackSize()), Actionable.MODULATE, new PlayerSource(player, t));

                            player.inventory.setInventorySlotContents(slot, aeitem.setStackSize(aeitem.getStackSize() + handItemConnt).createItemStack());
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private boolean securityCheck(final EntityPlayer player, IGrid gridNode, final SecurityPermissions requiredPermission) {
            final ISecurityGrid sg = gridNode.getCache(ISecurityGrid.class);
            return sg.hasPermission(player, requiredPermission);
        }

    }
}