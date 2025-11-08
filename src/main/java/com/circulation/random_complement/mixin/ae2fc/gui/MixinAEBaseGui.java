package com.circulation.random_complement.mixin.ae2fc.gui;

import appeng.client.gui.AEBaseGui;
import appeng.core.sync.packets.PacketInventoryAction;
import appeng.helpers.InventoryAction;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AEBaseGui.class)
public class MixinAEBaseGui {

    @Redirect(method = "handleMouseClick", at = @At(value = "NEW", target = "(Lappeng/helpers/InventoryAction;IJ)Lappeng/core/sync/packets/PacketInventoryAction;", ordinal = 0, remap = false))
    protected PacketInventoryAction handleMouseClick(InventoryAction action, int slot, long id) {
        return new PacketInventoryAction(action, slot, Mouse.getEventButton());
    }
}