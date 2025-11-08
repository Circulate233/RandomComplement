package com.circulation.random_complement.mixin.ae2fc.container;

import appeng.api.implementations.IUpgradeableCellContainer;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.helpers.InventoryAction;
import appeng.helpers.WirelessTerminalGuiObject;
import com.circulation.random_complement.mixin.ae2.container.MixinContainerWirelessPatternTerminal;
import com.glodblock.github.client.container.ContainerWirelessFluidPatternTerminal;
import com.glodblock.github.interfaces.PatternConsumer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerWirelessFluidPatternTerminal.class, remap = false)
public abstract class MixinContainerWirelessFluidPatternTerminal extends MixinContainerWirelessPatternTerminal implements PatternConsumer, IUpgradeableCellContainer, IInventorySlotAware {

    public MixinContainerWirelessFluidPatternTerminal(InventoryPlayer ip, WirelessTerminalGuiObject gui) {
        super(ip, gui);
    }

    @Inject(method = "doAction", at = @At("HEAD"), cancellable = true)
    public void doAction(EntityPlayerMP player, InventoryAction action, int slotId, long id, CallbackInfo ci) {
        if (id != 0) {
            super.doAction(player, action, slotId, 0);
            ci.cancel();
        }
    }

}