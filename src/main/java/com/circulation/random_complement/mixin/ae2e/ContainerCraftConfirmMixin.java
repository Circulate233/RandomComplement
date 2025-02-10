package com.circulation.random_complement.mixin.ae2e;

import appeng.api.parts.IPart;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.container.interfaces.IInventorySlotAware;
import com._0xc4de.ae2exttable.client.gui.PartGuiHandler;
import com._0xc4de.ae2exttable.interfaces.ITerminalGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value= ContainerCraftConfirm.class, remap=false)
public class ContainerCraftConfirmMixin extends AEBaseContainer {

    public ContainerCraftConfirmMixin(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    @Inject(method="startJob", at=@At(value="INVOKE", target="Lappeng/api/networking/crafting/ICraftingGrid;submitJob(Lappeng/api/networking/crafting/ICraftingJob;Lappeng/api/networking/crafting/ICraftingRequester;Lappeng/api/networking/crafting/ICraftingCPU;ZLappeng/api/networking/security/IActionSource;)Lappeng/api/networking/crafting/ICraftingLink;", shift=At.Shift.AFTER), cancellable=true)
    public void startJobMixin(CallbackInfo ci) {
        this.setAutoStart(false);
        TileEntity te = this.getOpenContext().getTile();
        if (te != null) {
            IPart terminal = PartGuiHandler.getPartFromWorld(te.getWorld(), te.getPos(), this.getOpenContext().getSide());
            if (terminal instanceof ITerminalGui t) {
                PartGuiHandler.openGUI(t.getGuiType(), this.getInventoryPlayer().player, te.getPos(), this.getOpenContext().getSide());
                ci.cancel();
            }
        } else if (this.obj.getItemStack().getItem() instanceof ITerminalGui t) {
            IInventorySlotAware i = ((IInventorySlotAware) this.obj);
            EntityPlayer player = this.getInventoryPlayer().player;
            PartGuiHandler.openWirelessTerminalGui(this.obj.getItemStack(), i.getInventorySlot(), i.isBaubleSlot(), player.world, player, t.getGuiType());
            ci.cancel();
        }
    }

    @Shadow
    public void setAutoStart(boolean b) {}
}
