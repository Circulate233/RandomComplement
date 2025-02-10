package com.circulation.random_complement.proxy;

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