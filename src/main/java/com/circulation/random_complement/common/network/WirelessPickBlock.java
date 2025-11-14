package com.circulation.random_complement.common.network;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.me.helpers.PlayerSource;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import baubles.api.BaublesApi;
import com.circulation.random_complement.common.interfaces.Packet;
import com.circulation.random_complement.common.util.MEHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WirelessPickBlock implements Packet<WirelessPickBlock> {

    private static final Map<UUID, Long> map = new ConcurrentHashMap<>();
    private int slot;
    private ItemStack stack = ItemStack.EMPTY;

    public WirelessPickBlock() {

    }

    public WirelessPickBlock(ItemStack stack, int slot) {
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

    @Override
    public IMessage onMessage(WirelessPickBlock message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        UUID playUUID = player.getUniqueID();
        Long worldTime = Instant.now().getEpochSecond();
        if (map.containsKey(playUUID)) {
            if (map.get(playUUID) < worldTime) {
                map.put(playUUID, worldTime);
            } else {
                player.sendMessage(new TextComponentTranslation("text.rc.warn"));
                return null;
            }
        } else {
            map.put(playUUID, worldTime);
        }
        final ItemStack handItem = player.inventory.getStackInSlot(message.slot);
        final ItemStack needItem = message.stack;
        if (!handItem.isEmpty()) {
            if (handItem.getCount() >= handItem.getItem().getItemStackLimit(handItem)) return null;
            else needItem.setCount(handItem.getItem().getItemStackLimit(handItem) - handItem.getCount());
        }

        player.getServer().addScheduledTask(() -> readPlayer(player, needItem, message));

        if (!needItem.isEmpty() && Loader.isModLoaded("baubles")) {
            player.getServer().addScheduledTask(() -> readBaubles(player, needItem, message.slot));
        }

        return null;
    }

    public void readPlayer(EntityPlayerMP player, ItemStack needItem, WirelessPickBlock message) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack item = player.inventory.getStackInSlot(i);
            if (item.getItem() instanceof IWirelessTermHandler wt && wt.canHandle(item)) {
                if (work(item, player, needItem, message.slot, i, 0)) {
                    needItem.setCount(0);
                    return;
                }
            }
        }
    }

    @Optional.Method(modid = "baubles")
    public void readBaubles(EntityPlayerMP player, ItemStack exitem, int slot) {
        for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            if (item.getItem() instanceof IWirelessTermHandler wt && wt.canHandle(item)) {
                if (work(item, player, exitem, slot, i, 1)) {
                    return;
                }
            }
        }
    }

    private boolean work(ItemStack item, EntityPlayerMP player, ItemStack exItem, int slot, int x, int y) {
        if (Platform.isClient()) return true;
        int handItemConnt = 0;
        if (!player.inventory.getStackInSlot(slot).isEmpty()) {
            ItemStack vitem = player.inventory.getStackInSlot(slot);
            if (exItem.getItem() != vitem.getItem() || exItem.getItemDamage() != vitem.getItemDamage() || exItem.getTagCompound() != vitem.getTagCompound())
                return true;
            handItemConnt = player.inventory.getStackInSlot(slot).getCount();
        }

        WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(item, player, x, y);

        if (obj == null) {
            return false;
        }

        if (!obj.rangeCheck()) {
            player.sendMessage(PlayerMessages.OutOfRange.get());
        } else {
            IGridNode gridNode = obj.getActionableNode();
            if (gridNode == null) return false;
            IGrid grid = gridNode.getGrid();
            if (securityCheck(player, grid, SecurityPermissions.EXTRACT)) {
                var items = grid.<IStorageGrid>getCache(IStorageGrid.class).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                var aeitem = items.extractItems(AEItemStack.fromItemStack(exItem).setStackSize(exItem.getCount()), Actionable.MODULATE, new PlayerSource(player, obj));
                if (aeitem != null) {
                    player.inventory.setInventorySlotContents(slot, aeitem.setStackSize(aeitem.getStackSize() + handItemConnt).createItemStack());
                    return true;
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