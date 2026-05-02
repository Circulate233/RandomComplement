package com.circulation.random_complement.mixin.ae2;

import appeng.core.sync.AppEngPacket;
import appeng.core.sync.AppEngPacketHandlerBase;
import appeng.core.sync.network.AppEngServerPacketHandler;
import com.circulation.random_complement.common.network.RCPacketMEInventoryUpdate;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import io.netty.buffer.ByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.IOException;

import static appeng.core.sync.AppEngPacketHandlerBase.PacketTypes.PACKET_ME_INVENTORY_UPDATE;

@Mixin(value = AppEngServerPacketHandler.class, remap = false)
public class MixinAppEngServerPacketHandler {

    @WrapOperation(method = "onPacketData", at = @At(value = "INVOKE", target = "Lappeng/core/sync/AppEngPacketHandlerBase$PacketTypes;getPacket(I)Lappeng/core/sync/AppEngPacketHandlerBase$PacketTypes;"))
    public AppEngPacketHandlerBase.PacketTypes getPacketR(int id, Operation<AppEngPacketHandlerBase.PacketTypes> original, @Share("r$id") LocalIntRef r$id) {
        return switch (id) {
            case 1000 -> {
                r$id.set(id);
                yield PACKET_ME_INVENTORY_UPDATE;
            }
            default -> original.call(id);
        };
    }

    @WrapOperation(method = "onPacketData", at = @At(value = "INVOKE", target = "Lappeng/core/sync/AppEngPacketHandlerBase$PacketTypes;parsePacket(Lio/netty/buffer/ByteBuf;)Lappeng/core/sync/AppEngPacket;"))
    public AppEngPacket getPacketR(AppEngPacketHandlerBase.PacketTypes instance, ByteBuf in, Operation<AppEngPacket> original, @Share("r$id") LocalIntRef r$id) throws IOException {
        return switch (r$id.get()) {
            case 1000 -> new RCPacketMEInventoryUpdate(in);
            default -> original.call(instance, in);
        };
    }
}
