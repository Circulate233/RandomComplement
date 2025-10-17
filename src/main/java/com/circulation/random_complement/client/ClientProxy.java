package com.circulation.random_complement.client;

import com.circulation.random_complement.client.handler.ItemTooltipHandler;
import com.circulation.random_complement.client.handler.RCInputHandler;
import com.circulation.random_complement.client.handler.RCJEIInputHandler;
import com.circulation.random_complement.common.CommonProxy;
import com.circulation.random_complement.common.util.Functions;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
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
        MinecraftForge.EVENT_BUS.register(new ItemTooltipHandler());
        if (Loader.isModLoaded("appliedenergistics2")) {
            MinecraftForge.EVENT_BUS.register(RCInputHandler.INSTANCE);
        }
        if (Loader.isModLoaded("jei")) {
            ReferenceList<Class<?>> classes = new ReferenceArrayList<>();
            if (Loader.isModLoaded("appliedenergistics2")) {
                try {
                    classes.add(Class.forName("appeng.client.gui.AEBaseGui"));
                } catch (ClassNotFoundException ignored) {

                }
            }
            if (Loader.isModLoaded("packagedauto")) {
                try {
                    classes.add(Class.forName("thelm.packagedauto.client.gui.GuiEncoder"));
                } catch (ClassNotFoundException ignored) {

                }
            }
            if (!classes.isEmpty()) {
                RCJEIInputHandler.setJeiGui(classes.toArray(new Class[0]));
                MinecraftForge.EVENT_BUS.register(RCJEIInputHandler.INSTANCE);
            }
        }
    }

    @Override
    public void init() {
        super.init();
        if (Functions.modLoaded("appliedenergistics2")) {
            KeyBindings.init();
        }
    }

    @Override
    public void postInit() {
        super.postInit();
        RegItemTooltip.regAll();
    }

    @SubscribeEvent
    public void onRegisterModels(ModelRegistryEvent event) {

    }

}