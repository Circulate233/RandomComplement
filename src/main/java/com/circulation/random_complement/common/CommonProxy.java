package com.circulation.random_complement.common;

import com.circulation.random_complement.common.handler.CraftingUnitHandler;
import com.circulation.random_complement.common.network.ContainerRollBACK;
import com.circulation.random_complement.common.network.KeyBindingHandler;
import com.circulation.random_complement.common.network.RCActionButton;
import com.circulation.random_complement.common.network.RCConfigButton;
import com.circulation.random_complement.common.network.SyncConfig;
import com.circulation.random_complement.common.network.WirelessPickBlock;
import com.circulation.random_complement.common.util.Functions;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;

import static com.circulation.random_complement.RandomComplement.NET_CHANNEL;

@SuppressWarnings("MethodMayBeStatic")
public class CommonProxy {

    public void construction() {
    }

    public void preInit() {
        int start = 0;
        if (Functions.modLoaded("appliedenergistics2")) {
            NET_CHANNEL.registerMessage(ContainerRollBACK.class, ContainerRollBACK.class, start++, Side.CLIENT);
            NET_CHANNEL.registerMessage(SyncConfig.class, SyncConfig.class, start++, Side.CLIENT);

            NET_CHANNEL.registerMessage(ContainerRollBACK.class, ContainerRollBACK.class, start++, Side.SERVER);
            NET_CHANNEL.registerMessage(WirelessPickBlock.class, WirelessPickBlock.class, start++, Side.SERVER);
            NET_CHANNEL.registerMessage(RCConfigButton.class, RCConfigButton.class, start++, Side.SERVER);
            NET_CHANNEL.registerMessage(RCActionButton.class, RCActionButton.class, start++, Side.SERVER);
            if (Functions.modLoaded("jei")) {
                NET_CHANNEL.registerMessage(KeyBindingHandler.class, KeyBindingHandler.class, start++, Side.SERVER);
            }
        }
    }

    public void init() {
    }

    public void postInit() {
        if (Loader.isModLoaded("appliedenergistics2")) CraftingUnitHandler.register();
    }

    @Optional.Method(modid = "jei")
    public boolean isMouseHasItem() {
        return false;
    }

}