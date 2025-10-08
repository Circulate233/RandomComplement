package com.circulation.random_complement.client;

import com.circulation.random_complement.client.handler.RCInputHandler;
import com.circulation.random_complement.client.handler.RCJEIInputHandler;
import com.circulation.random_complement.common.CommonProxy;
import com.circulation.random_complement.common.util.Function;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

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
            MinecraftForge.EVENT_BUS.register(RCInputHandler.INSTANCE);
        }
        if (Loader.isModLoaded("jei")) {
            if (Loader.isModLoaded("appliedenergistics2")) {
                try {
                    RCJEIInputHandler.addJeiGui(Class.forName("appeng.client.gui.AEBaseGui"));
                } catch (ClassNotFoundException ignored) {

                }
            }
            if (Loader.isModLoaded("packagedauto")) {
                try {
                    RCJEIInputHandler.addJeiGui(Class.forName("thelm.packagedauto.client.gui.GuiEncoder"));
                } catch (ClassNotFoundException ignored) {

                }
            }
            if (RCJEIInputHandler.getJeiGuiSize() > 0) {
                MinecraftForge.EVENT_BUS.register(RCJEIInputHandler.INSTANCE);
            }
        }
    }

    @Override
    public void init() {
        super.init();
        if (Function.modLoaded("appliedenergistics2")) {
            KeyBindings.init();
        }
    }

    @Override
    public void postInit() {
        super.postInit();
    }

    @SubscribeEvent
    public void onRegisterModels(ModelRegistryEvent event) {

    }

}