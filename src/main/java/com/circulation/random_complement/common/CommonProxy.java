package com.circulation.random_complement.common;

import com.circulation.random_complement.common.handler.CraftingUnitHandler;
import com.circulation.random_complement.common.interfaces.Packet;
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

    private int id = 0;

    public void construction() {
    }

    public void preInit() {
        if (Functions.modLoaded("appliedenergistics2")) {
            registerMessage(ContainerRollBACK.class, Side.CLIENT);
            registerMessage(SyncConfig.class, Side.CLIENT);

            registerMessage(ContainerRollBACK.class, Side.SERVER);
            registerMessage(WirelessPickBlock.class, Side.SERVER);
            registerMessage(RCConfigButton.class, Side.SERVER);
            registerMessage(RCActionButton.class, Side.SERVER);
            if (Functions.modLoaded("jei")) {
                registerMessage(KeyBindingHandler.class, Side.SERVER);
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

    public <T extends Packet<T>> void registerMessage(Class<T> aClass, Side side) {
        NET_CHANNEL.registerMessage(aClass, aClass, id++, side);
    }

}