package com.circulation.random_complement.common.network;

import com.circulation.random_complement.common.interfaces.Packet;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.circulation.random_complement.RCConfig.Botania;
import static com.circulation.random_complement.RCConfig.IC2;
import static com.circulation.random_complement.RCConfig.NEE;
import static com.circulation.random_complement.RCConfig.TE5;

public class SyncConfig implements Packet<SyncConfig> {

    @Override
    public void fromBytes(ByteBuf buf) {
        NEE.ae2e = buf.readBoolean();
        IC2.overclockerEnergy = buf.readDouble();
        IC2.overclockerTime = buf.readDouble();
        IC2.energyStorageEnergy = buf.readInt();
        TE5.FuelCatalyzer = buf.readInt();
        Botania.FlowerLinkPool = buf.readBoolean();
        Botania.SparkSupport = buf.readBoolean();
        Botania.RuneAltarSparkSupport = buf.readBoolean();
        Botania.BrewerySparkSupport = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(NEE.ae2e);
        buf.writeDouble(IC2.overclockerEnergy);
        buf.writeDouble(IC2.overclockerTime);
        buf.writeInt(IC2.energyStorageEnergy);
        buf.writeInt(TE5.FuelCatalyzer);
        buf.writeBoolean(Botania.FlowerLinkPool);
        buf.writeBoolean(Botania.SparkSupport);
        buf.writeBoolean(Botania.RuneAltarSparkSupport);
        buf.writeBoolean(Botania.BrewerySparkSupport);
    }

    @Override
    public IMessage onMessage(SyncConfig message, MessageContext ctx) {
        return null;
    }

}
