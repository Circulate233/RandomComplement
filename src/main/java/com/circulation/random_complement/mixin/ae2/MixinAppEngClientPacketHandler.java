package com.circulation.random_complement.mixin.ae2;

import appeng.core.sync.AppEngPacket;
import appeng.core.sync.AppEngPacketHandlerBase;
import appeng.core.sync.network.AppEngClientPacketHandler;
import com.circulation.random_complement.common.network.RCPacketMEInventoryUpdate;
import io.netty.buffer.ByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static appeng.core.sync.AppEngPacketHandlerBase.PacketTypes.PACKET_ME_INVENTORY_UPDATE;

@Mixin(value = AppEngClientPacketHandler.class,remap = false)
public class MixinAppEngClientPacketHandler {

    @Unique
    private int randomComplement$id;

    @Redirect(method = "onPacketData",at = @At(value = "INVOKE", target = "Lappeng/core/sync/AppEngPacketHandlerBase$PacketTypes;getPacket(I)Lappeng/core/sync/AppEngPacketHandlerBase$PacketTypes;"))
    public AppEngPacketHandlerBase.PacketTypes getPacketR(int id){
        randomComplement$id = 0;
        return switch (id){
            case 1000 -> {
                this.randomComplement$id = id;
                yield PACKET_ME_INVENTORY_UPDATE;
            }
            default -> AppEngPacketHandlerBase.PacketTypes.getPacket(id);
        };
    }

    @Redirect(method = "onPacketData",at = @At(value = "INVOKE", target = "Lappeng/core/sync/AppEngPacketHandlerBase$PacketTypes;parsePacket(Lio/netty/buffer/ByteBuf;)Lappeng/core/sync/AppEngPacket;"))
    public AppEngPacket getPacketR(AppEngPacketHandlerBase.PacketTypes instance, ByteBuf in) throws IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return switch (randomComplement$id){
            case 1000 -> new RCPacketMEInventoryUpdate(in);
            default -> instance.parsePacket(in);
        };
    }
}
