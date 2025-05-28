package com.circulation.random_complement.client;

import com.circulation.random_complement.client.handler.InputHandler;
import com.circulation.random_complement.common.CommonProxy;
import com.circulation.random_complement.common.util.Function;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("MethodMayBeStatic")
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    public static final String categoryJEI = "RandomComplement(JEI)";

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
        if (Function.modLoaded("appliedenergistics2")){
            KeyBindings.init();
        }
    }

    @Override
    public void postInit() {
        super.postInit();
    }

}