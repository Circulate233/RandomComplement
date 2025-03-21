package com.circulation.random_complement.client;

import com.circulation.random_complement.client.handler.InputHandler;
import com.circulation.random_complement.common.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("MethodMayBeStatic")
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void construction() {
        super.construction();
    }

    @Override
    public void preInit() {
        super.preInit();
        if (Loader.isModLoaded("appliedenergistics2")) {
            MinecraftForge.EVENT_BUS.register(InputHandler.INSTANCE);
        }
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void postInit() {
        super.postInit();
    }

}