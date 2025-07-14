package com.circulation.random_complement.mixin.ae2;

import appeng.core.sync.AppEngPacket;
import appeng.core.sync.AppEngPacketHandlerBase;
import com.circulation.random_complement.common.network.RCPacketMEInventoryUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AppEngPacketHandlerBase.PacketTypes.class,remap = false)
public class MixinPacketTypes {

    @Inject(method = "getID",at = @At("HEAD"), cancellable = true)
    private static void getIDMixin(Class<? extends AppEngPacket> c, CallbackInfoReturnable<AppEngPacketHandlerBase.PacketTypes> cir) {
        if (c == RCPacketMEInventoryUpdate.class){
            cir.setReturnValue(AppEngPacketHandlerBase.PacketTypes.PACKET_ME_INVENTORY_UPDATE);
        }
    }
}
