package com.circulation.random_complement.mixin.ae2;

import appeng.core.sync.AppEngPacket;
import appeng.core.sync.AppEngPacketHandlerBase;
import appeng.core.sync.network.AppEngServerPacketHandler;
import com.circulation.random_complement.common.network.RCPacketMEInventoryUpdate;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.buffer.ByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.IOException;

import static appeng.core.sync.AppEngPacketHandlerBase.PacketTypes.PACKET_ME_INVENTORY_UPDATE;

@Mixin(value = AppEngServerPacketHandler.class, remap = false)
public class MixinAppEngServerPacketHandler {

    @Unique
    private int randomComplement$id;

    @WrapOperation(method = "onPacketData", at = @At(value = "INVOKE", target = "Lappeng/core/sync/AppEngPacketHandlerBase$PacketTypes;getPacket(I)Lappeng/core/sync/AppEngPacketHandlerBase$PacketTypes;"))
    public AppEngPacketHandlerBase.PacketTypes getPacketR(int id, Operation<AppEngPacketHandlerBase.PacketTypes> original) {
        randomComplement$id = 0;
        return switch (id) {
            case 1000 -> {
                this.randomComplement$id = id;
                yield PACKET_ME_INVENTORY_UPDATE;
            }
            default -> original.call(id);
        };
    }

    @WrapOperation(method = "onPacketData", at = @At(value = "INVOKE", target = "Lappeng/core/sync/AppEngPacketHandlerBase$PacketTypes;parsePacket(Lio/netty/buffer/ByteBuf;)Lappeng/core/sync/AppEngPacket;"))
    public AppEngPacket getPacketR(AppEngPacketHandlerBase.PacketTypes instance, ByteBuf in, Operation<AppEngPacket> original) throws IOException {
        return switch (randomComplement$id) {
            case 1000 -> new RCPacketMEInventoryUpdate(in);
            default -> original.call(instance, in);
        };
    }
}
