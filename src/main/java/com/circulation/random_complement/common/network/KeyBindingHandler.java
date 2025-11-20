package com.circulation.random_complement.common.network;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.implementations.ContainerCraftAmount;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.core.localization.PlayerMessages;
import appeng.core.sync.GuiBridge;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.me.helpers.PlayerSource;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import baubles.api.BaublesApi;
import com.circulation.random_complement.common.interfaces.Packet;
import com.circulation.random_complement.common.interfaces.RCAEBaseContainer;
import com.circulation.random_complement.common.interfaces.RCCraftingGridCache;
import com.circulation.random_complement.common.util.MEHandler;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class KeyBindingHandler implements Packet<KeyBindingHandler> {

    ItemStack stack = ItemStack.EMPTY;
    String key;
    boolean isAE = false;

    public KeyBindingHandler() {

    }

    public KeyBindingHandler(String key) {
        this.key = key;
    }

    public KeyBindingHandler(String key, ItemStack item, boolean isAE) {
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
        ByteBufUtils.writeUTF8String(buf, this.key);
        buf.writeBoolean(this.isAE);
    }

    @Override
    public IMessage onMessage(KeyBindingHandler message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        var container = player.openContainer;
        var item = message.stack;
        switch (message.key) {
            case "RetrieveItem" -> retrieveItem(player, container, item, message.isAE);
            case "StartCraft" ->
                player.getServer().addScheduledTask(() -> startCraft(player, container, item, message.isAE));
        }
        return null;
    }

    private void retrieveItem(EntityPlayerMP player, Container container, ItemStack item, boolean isAE) {
        long targetCount = item.getMaxStackSize();
        if (!isAE) {
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack ii = player.inventory.getStackInSlot(i);
                WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(ii, player, i, 0);

                if (obj == null) {
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
                    if (targetCount <= 0) {
                        return;
                    }
                }
            }
            if (Loader.isModLoaded("baubles")) {
                readBaublesR(player, item, targetCount);
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
                var items = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                var host = c.getTarget();
                if (host instanceof IActionHost h) {
                    var aeitem = items.extractItems(AEItemStack.fromItemStack(item).setStackSize(targetCount), Actionable.MODULATE, new PlayerSource(player, h));
                    if (aeitem != null) {
                        player.inventory.placeItemBackInInventory(player.world, aeitem.createItemStack());
                    }
                }
            }
        }
    }

    private long wirelessRetrieve(EntityPlayerMP player, ItemStack exItem, IGridNode gridNode, long targetCount, WirelessTerminalGuiObject obj) {
        IGrid grid = gridNode.getGrid();
        if (securityCheck(player, grid, SecurityPermissions.EXTRACT)) {
            var items = grid.<IStorageGrid>getCache(IStorageGrid.class).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            var aeitem = items.extractItems(AEItemStack.fromItemStack(exItem).setStackSize(targetCount), Actionable.MODULATE, new PlayerSource(player, obj));

            if (aeitem == null) return targetCount;

            targetCount -= aeitem.getStackSize();

            player.inventory.placeItemBackInInventory(player.world, aeitem.createItemStack());
        }
        return targetCount;
    }

    private void startCraft(EntityPlayerMP player, Container container, ItemStack item, boolean isAE) {
        item.setCount(1);
        if (!isAE) {
            if (player.openContainer instanceof ContainerCraftAmount
                || player.openContainer instanceof ContainerCraftConfirm) return;
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack ii = player.inventory.getStackInSlot(i);
                WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(ii, player, i, 0);

                if (obj == null) {
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
                    openWirelessCraft(ii, player, item, gridNode, i, false);
                    return;
                }
            }
            if (Loader.isModLoaded("baubles")) {
                readBaublesS(player, item);
            }
        } else if (container instanceof ContainerMEMonitorable c) {
            IGridNode gridNode = c.getNetworkNode();
            if (gridNode == null) {
                player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                return;
            }
            IGrid grid = gridNode.getGrid();
            if (securityCheck(player, grid, SecurityPermissions.CRAFT)) {
                RCCraftingGridCache cgc = grid.getCache(ICraftingGrid.class);
                IAEItemStack aeItem = AEItemStack.fromItemStack(item).setStackSize(1);
                boolean isCraftable = cgc.rc$getCraftableItems().containsKey(aeItem);

                if (!isCraftable) {
                    player.sendMessage(new TextComponentTranslation("text.rc.craft"));
                    return;
                }

                var host = c.getTarget();
                if (host instanceof IActionHost i) {
                    if (Loader.isModLoaded("ae2fc")) ae2fcCraft(i, player, c);
                    else
                        Platform.openGUI(player, c.getOpenContext().getTile(), c.getOpenContext().getSide(), GuiBridge.GUI_CRAFTING_AMOUNT);

                    if (player.openContainer instanceof ContainerCraftAmount cca) {
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
            RCCraftingGridCache cgc = gridNode.getGrid().getCache(ICraftingGrid.class);
            IAEItemStack aeItem = AEItemStack.fromItemStack(exItem).setStackSize(1);
            boolean isCraftable = cgc.rc$getCraftableItems().containsKey(aeItem);

            if (!isCraftable) {
                player.sendMessage(new TextComponentTranslation("text.rc.craft"));
                return;
            }

            final var oldContainer = player.openContainer;

            Platform.openGUI(player, i, GuiBridge.GUI_CRAFTING_AMOUNT, isBauble);

            var newContainer = player.openContainer;

            if (newContainer instanceof ContainerCraftAmount cca) {
                if (newContainer instanceof RCAEBaseContainer rcc) {
                    rcc.rc$setOldContainer(oldContainer);
                }
                cca.getCraftingItem().putStack(aeItem.asItemStackRepresentation());
                cca.setItemToCraft(aeItem);
                cca.detectAndSendChanges();
            }
        }
    }

    @Method(modid = "ae2fc")
    private void ae2fcCraft(IActionHost host, EntityPlayerMP player, ContainerMEMonitorable c) {
        if (host instanceof PartExtendedFluidPatternTerminal
            || host instanceof PartFluidPatternTerminal
            || (host instanceof WirelessTerminalGuiObject w &&
            (w.getItemStack().getItem() instanceof ItemWirelessFluidPatternTerminal || (w.getItemStack().hasTagCompound() && w.getItemStack().getTagCompound().getByte("mode") == 4)))) {
            var context = c.getOpenContext();
            player.getServerWorld().addScheduledTask(() -> InventoryHandler.openGui(player, Ae2Reflect.getContextWorld(context), new BlockPos(Ae2Reflect.getContextX(context), Ae2Reflect.getContextY(context), Ae2Reflect.getContextZ(context)), context.getSide().getFacing(), GuiType.FLUID_CRAFT_AMOUNT));
            return;
        }
        Platform.openGUI(player, c.getOpenContext().getTile(), c.getOpenContext().getSide(), GuiBridge.GUI_CRAFTING_AMOUNT);
    }

    @Method(modid = "baubles")
    private void readBaublesS(EntityPlayerMP player, ItemStack exitem) {
        for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(item, player, i, 1);

            if (obj == null) {
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
                openWirelessCraft(item, player, exitem, gridNode, i, true);
                return;
            }
        }
    }

    @Method(modid = "baubles")
    private void readBaublesR(EntityPlayerMP player, ItemStack exitem, long targetCount) {
        for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(item, player, i, 1);

            if (obj == null) {
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
                if (targetCount <= 0) {
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
