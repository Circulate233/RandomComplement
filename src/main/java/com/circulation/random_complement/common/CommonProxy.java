package com.circulation.random_complement.common;

import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.common.handler.CraftingUnitHandler;
import com.circulation.random_complement.common.network.ContainerRollBACK;
import com.circulation.random_complement.common.network.KeyBindingHandler;
import com.circulation.random_complement.common.network.RCActionButton;
import com.circulation.random_complement.common.network.RCConfigButton;
import com.circulation.random_complement.common.network.SyncConfig;
import com.circulation.random_complement.common.network.WirelessPickBlock;
import com.circulation.random_complement.common.util.Function;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

import static com.circulation.random_complement.RandomComplement.NET_CHANNEL;

@SuppressWarnings("MethodMayBeStatic")
public class CommonProxy {

    public void construction() {
    }

    public void preInit() {
        int start = 0;
        MinecraftForge.EVENT_BUS.register(this);
        if (Function.modLoaded("appliedenergistics2")) {
            NET_CHANNEL.registerMessage(ContainerRollBACK.class, ContainerRollBACK.class, start++, Side.CLIENT);
            NET_CHANNEL.registerMessage(SyncConfig.class, SyncConfig.class, start++, Side.CLIENT);

            NET_CHANNEL.registerMessage(ContainerRollBACK.class, ContainerRollBACK.class, start++, Side.SERVER);
            NET_CHANNEL.registerMessage(WirelessPickBlock.class, WirelessPickBlock.class, start++, Side.SERVER);
            NET_CHANNEL.registerMessage(RCConfigButton.class, RCConfigButton.class, start++, Side.SERVER);
            NET_CHANNEL.registerMessage(RCActionButton.class, RCActionButton.class, start++, Side.SERVER);
            if (Function.modLoaded("jei")) {
                NET_CHANNEL.registerMessage(KeyBindingHandler.class, KeyBindingHandler.class, start++, Side.SERVER);
            }
        }
    }

    public void init() {
    }

    public void postInit() {
        if (Loader.isModLoaded("appliedenergistics2")) CraftingUnitHandler.register();
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP mp) {
            NET_CHANNEL.sendTo(new SyncConfig(), mp);
        }
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(RandomComplement.MOD_ID)) {
            ConfigManager.sync(RandomComplement.MOD_ID, Config.Type.INSTANCE);
            if (RandomComplement.server != null && FMLCommonHandler.instance().getSide().isServer()) {
                for (EntityPlayerMP player : RandomComplement.server.getPlayerList().getPlayers()) {
                    RandomComplement.NET_CHANNEL.sendTo(new SyncConfig(), player);
                }
            }
        }
    }

}