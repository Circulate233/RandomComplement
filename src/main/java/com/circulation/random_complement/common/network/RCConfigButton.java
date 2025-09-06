package com.circulation.random_complement.common.network;

import appeng.container.AEBaseContainer;
import appeng.util.Platform;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.common.interfaces.Packet;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RCConfigButton implements Packet<RCConfigButton> {

    private RCSettings option;
    private boolean rotationDirection;

    public RCConfigButton() {

    }

    public RCConfigButton(RCSettings option, boolean rotationDirection) {
        this.option = option;
        this.rotationDirection = rotationDirection;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.option = RCSettings.values()[buf.readInt()];
        this.rotationDirection = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(option.ordinal());
        buf.writeBoolean(rotationDirection);
    }

    @Override
    public IMessage onMessage(RCConfigButton message, MessageContext ctx) {
        EntityPlayerMP sender = ctx.getServerHandler().player;
        if (sender.openContainer instanceof AEBaseContainer baseContainer) {
            if (baseContainer.getTarget() instanceof RCIConfigurableObject obj) {
                RCIConfigManager cm = obj.r$getConfigManager();
                Enum<?> newState = Platform.rotateEnum(cm.getSetting(message.option), message.rotationDirection, message.option.getValues());
                cm.putSetting(message.option, newState);
            }
        }
        return null;
    }
}