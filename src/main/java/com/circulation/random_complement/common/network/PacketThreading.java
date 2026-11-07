package com.circulation.random_complement.common.network;

import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

final class PacketThreading {

    private PacketThreading() {
    }

    static boolean enqueueIfNeeded(MessageContext context, Runnable handler) {
        return enqueueIfNeeded(FMLCommonHandler.instance().getWorldThread(context.netHandler), handler);
    }

    static boolean enqueueIfNeeded(IThreadListener thread, Runnable handler) {
        if (thread.isCallingFromMinecraftThread()) {
            return false;
        }
        thread.addScheduledTask(handler);
        return true;
    }
}
