package com.circulation.random_complement.mixin.ae2fc.container;

import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerPatternTerm;
import appeng.helpers.InventoryAction;
import com.glodblock.github.client.container.ContainerFluidPatternTerminal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerFluidPatternTerminal.class, remap = false)
public class MixinContainerFluidPatternTerminal extends ContainerPatternTerm {

    public MixinContainerFluidPatternTerminal(InventoryPlayer ip, ITerminalHost monitorable) {
        super(ip, monitorable);
    }

    @Inject(method = "doAction", at = @At("HEAD"), cancellable = true)
    public void doAction(EntityPlayerMP player, InventoryAction action, int slotId, long id, CallbackInfo ci) {
        if (id != 0) {
            super.doAction(player, action, slotId, 0);
            ci.cancel();
        }
    }
}