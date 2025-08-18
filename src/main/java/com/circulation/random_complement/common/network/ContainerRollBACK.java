package com.circulation.random_complement.common.network;

import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.handler.InputHandler;
import com.circulation.random_complement.common.util.Packet;
import com.circulation.random_complement.common.util.RCAEBaseContainer;
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
                entityPlayerMP.getServer().addScheduledTask(() -> {
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
                });
            }
            case CLIENT -> ClientRun();
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void ClientRun() {
        if (InputHandler.delayMethod != null) {
            InputHandler.delayMethod.run();
            InputHandler.delayMethod = null;
            InputHandler.oldGui = null;
        }
    }
}
