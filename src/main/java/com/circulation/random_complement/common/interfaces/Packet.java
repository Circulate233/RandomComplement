package com.circulation.random_complement.common.interfaces;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public interface Packet<T extends IMessage> extends IMessage, IMessageHandler<T, IMessage> {
}
