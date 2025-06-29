package com.circulation.random_complement.mixin.fluxnetworks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sonar.fluxnetworks.common.connection.transfer.ConnectionTransfer;
import sonar.fluxnetworks.common.connection.transfer.FluxPointHandler;

@Mixin(value = FluxPointHandler.class,remap = false)
public abstract class MixinFluxPointHandler{

    @Redirect(method = "sendToConsumers",at = @At(value = "INVOKE", target = "Lsonar/fluxnetworks/common/connection/transfer/ConnectionTransfer;sendToTile(JZ)J"))
    public long sendToConsumersRedirect(ConnectionTransfer instance, long leftover, boolean simulate) {
        return Math.max(instance.sendToTile(leftover,simulate),0);
    }
}