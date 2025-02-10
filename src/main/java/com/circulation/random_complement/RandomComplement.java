package com.circulation.random_complement;

import com.circulation.random_complement.proxy.CommonProxy;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "random_complement", name = Tags.MOD_NAME, version = Tags.VERSION,
        dependencies = "required:mixinbooter@[8.0,);" +
                       "required:appliedenergistics2@[v0.56.5,);"
)
public class RandomComplement {

    public static final String MOD_ID = "random_complement";
    public static final String CLIENT_PROXY = "com.circulation.random_complement.proxy.ClientProxy";
    public static final String COMMON_PROXY = "com.circulation.random_complement.proxy.CommonProxy";

    public static final SimpleNetworkWrapper NET_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy = null;

    @Mod.Instance(MOD_ID)
    public static RandomComplement instance = null;
    public static LogWrapper logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        int start = 0;

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

}
