package com.circulation.random_complement.common.network;

import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.handler.RCInputHandler;
import com.circulation.random_complement.common.interfaces.Packet;
import com.circulation.random_complement.common.interfaces.RCAEBaseContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerRollBACK implements Packet<ContainerRollBACK> {

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(ContainerRollBACK message, MessageContext ctx) {
        switch (ctx.side) {
            case SERVER -> {
                var entityPlayerMP = ctx.getServerHandler().player;
                var newContainer = entityPlayerMP.openContainer;
                if (newContainer instanceof RCAEBaseContainer rac) {
                    var oldContainer = rac.rc$getOldContainer();
                    if (oldContainer != null) {
                        if (oldContainer instanceof ContainerPlayer) {
                            entityPlayerMP.closeContainer();
                        } else {
                            entityPlayerMP.getNextWindowId();
                            entityPlayerMP.closeContainer();
                            int windowId = entityPlayerMP.currentWindowId;
                            entityPlayerMP.openContainer = oldContainer;
                            entityPlayerMP.openContainer.windowId = windowId;
                        }
                    }
                }
                RandomComplement.NET_CHANNEL.sendTo(message, entityPlayerMP);
            }
            case CLIENT -> ClientRun();
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void ClientRun() {
        if (RCInputHandler.delayMethod != null) {
            RCInputHandler.delayMethod.run();
            RCInputHandler.delayMethod = null;
            RCInputHandler.oldGui = null;
        }
    }
}
