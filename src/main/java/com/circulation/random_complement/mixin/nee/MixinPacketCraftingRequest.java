package com.circulation.random_complement.mixin.nee;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.ISecurityGrid;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.container.interfaces.IInventorySlotAware;
import com.github.vfyjxf.nee.container.ContainerCraftingAmount;
import com.github.vfyjxf.nee.network.NEEGuiHandler;
import com.github.vfyjxf.nee.network.packet.PacketCraftingRequest;
import com.github.vfyjxf.nee.utils.GuiUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PacketCraftingRequest.Handler.class, remap = false)
public abstract class MixinPacketCraftingRequest implements IMessageHandler<PacketCraftingRequest, IMessage> {

    @Inject(method = "unofficialHelper", at = @At(value = "HEAD"), cancellable = true)
    private static void unofficialHelper(IActionHost host, EntityPlayer player, CallbackInfo ci) {
        if (host instanceof IInventorySlotAware slotAware) {
            NEEGuiHandler.openWirelessGui(player, NEEGuiHandler.WIRELESS_CRAFTING_CONFIRM_UNOFFICIAL_ID, slotAware.getInventorySlot(), slotAware.isBaubleSlot());
            ci.cancel();
        }
    }

    /**
     * @author Circulation_
     * @reason 修改方法使得兼容ae2exttable, 并且支持AE2UEL的无线合成终端
     */
    @Overwrite
    public IMessage onMessage(PacketCraftingRequest message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        Container container = player.openContainer;
        if (container instanceof AEBaseContainer baseContainer) {
            player.getServerWorld().addScheduledTask(() -> {
                Object target = baseContainer.getTarget();
                if (target instanceof IActionHost ah) {
                    IGridNode gn = ah.getActionableNode();
                    IGrid grid = gn.getGrid();
                    ISecurityGrid security = grid.getCache(ISecurityGrid.class);
                    if (security.hasPermission(player, SecurityPermissions.CRAFT)) {
                        if (baseContainer instanceof ContainerMEMonitorable) {
                            this.handlerCraftingTermRequest(baseContainer, message, grid, ah, player);
                        }
                        if (baseContainer instanceof ContainerCraftingAmount c) {
                            this.handlerCraftingAmountRequest(c, message, grid, player);
                        }
                        if (GuiUtils.isWirelessCraftingTermContainer(container)) {
                            this.handlerWirelessCraftingRequest(baseContainer, message, grid, player);
                        }
                    }
                }
            });
        }
        return null;
    }

    @Shadow
    private void handlerCraftingTermRequest(AEBaseContainer container, PacketCraftingRequest message, IGrid grid, IActionHost ah, EntityPlayerMP player) {
    }

    @Shadow
    private void handlerCraftingAmountRequest(ContainerCraftingAmount container, PacketCraftingRequest message, IGrid grid, EntityPlayerMP player) {
    }

    @Shadow
    private void handlerWirelessCraftingRequest(AEBaseContainer container, PacketCraftingRequest message, IGrid grid, EntityPlayerMP player) {
    }
}
