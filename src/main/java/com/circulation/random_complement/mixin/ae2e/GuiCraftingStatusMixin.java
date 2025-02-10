package com.circulation.random_complement.mixin.ae2e;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiCraftingStatus;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.helpers.WirelessTerminalGuiObject;
import com._0xc4de.ae2exttable.client.gui.AE2ExtendedGUIs;
import com._0xc4de.ae2exttable.interfaces.ITerminalGui;
import com._0xc4de.ae2exttable.network.ExtendedTerminalNetworkHandler;
import com._0xc4de.ae2exttable.network.packets.PacketSwitchGui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=GuiCraftingStatus.class, remap=false)
public class GuiCraftingStatusMixin {

    @Shadow
    private GuiTabButton originalGuiBtn;

    @Unique
    private AE2ExtendedGUIs randomComplement$extendedOriginalGui;

    @SuppressWarnings("InjectIntoConstructor")
    @Inject(method="<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lappeng/api/storage/ITerminalHost;)V",
            at= @At(value = "INVOKE", target = "Lappeng/api/definitions/IDefinitions;parts()Lappeng/api/definitions/IParts;", shift = At.Shift.AFTER))
    private void onInit(final InventoryPlayer inventoryPlayer, final ITerminalHost te, CallbackInfo ci) {
        if (te instanceof WirelessTerminalGuiObject wt) {
            ItemStack item = wt.getItemStack();
            if (item.getItem() instanceof ITerminalGui bt) {
                this.randomComplement$extendedOriginalGui = bt.getGuiType();
                return;
            }
        }
        if (te instanceof ITerminalGui term) {
            this.randomComplement$extendedOriginalGui = term.getGuiType();
        }
    }

    @Inject(method = "actionPerformed", at = @At(value="HEAD"), cancellable=true, remap=true)
    protected void actionPerformed(GuiButton btn, CallbackInfo ci) {
        // Defined if the terminal host is one of my crafting terminals
        if (this.randomComplement$extendedOriginalGui != null) {
            if (btn == this.originalGuiBtn) {
                ExtendedTerminalNetworkHandler.instance().sendToServer(new PacketSwitchGui(this.randomComplement$extendedOriginalGui));
                ci.cancel();
            }
        }
    }
}

