package com.circulation.random_complement;

import com.circulation.random_complement.common.network.SyncConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import static com.circulation.random_complement.RandomComplement.NET_CHANNEL;

@Config(modid = RandomComplement.MOD_ID)
@Mod.EventBusSubscriber(modid = RandomComplement.MOD_ID)
public class RCConfig {

    @Config.Name("AE2")
    public static final ae2 AE2 = new ae2();

    @Config.Name("IC2")
    public static final IC2 IC2 = new IC2();

    @Config.Name("LazyAE")
    public static final LazyAE LazyAE = new LazyAE();

    @Config.Name("TE5")
    public static final TE5 TE5 = new TE5();

    @Config.Name("FTBU")
    public static final FTBU FTBU = new FTBU();

    @Config.Name("TF5")
    public static final TF5 TF5 = new TF5();

    @Config.Name("Botania")
    public static final Botania Botania = new Botania();

    @Config.Name("COFHCOHE")
    public static final COFHCORE COFHCORE = new COFHCORE();

    @Config.Name("NEE")
    public static final NEE NEE = new NEE();

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(RandomComplement.MOD_ID)) {
            ConfigManager.sync(RandomComplement.MOD_ID, Config.Type.INSTANCE);
            if (FMLCommonHandler.instance().getSide().isServer()) {
                for (EntityPlayerMP player : FMLServerHandler.instance().getServer().getPlayerList().getPlayers()) {
                    RandomComplement.NET_CHANNEL.sendTo(new SyncConfig(), player);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP mp) {
            NET_CHANNEL.sendTo(new SyncConfig(), mp);
        }
    }

    public static class NEE {

        @Config.Comment({"Whether to enable NEE compatibility for ae2exttable."})
        @Config.Name("ae2e")
        public boolean ae2e = true;

    }

    public static class ae2 {
        @Config.Comment({"Disable the Build permission check for AE2's security station"})
        @Config.Name("SecurityCache")
        public boolean SecurityCache = true;

        @Config.Comment({"Sets the texture index used for the crafting item slot"})
        @Config.Name("CraftingSlotTextureIndex")
        @Config.RangeInt(min = 0, max = 6)
        public int craftingSlotTextureIndex = 1;

        @Config.Comment({"Make the grid always think it's full of energy"})
        @Config.Name("debugEnergy")
        public boolean debugEnergy = false;

        @Config.Comment({"Use the new Patten Terminal Gui"})
        @Config.Name("newPattenGui")
        public boolean newPattenGui = true;
    }

    public static class IC2 {
        @Config.Comment({"The increased energy consumption caused by each Overclocker Upgrade"})
        @Config.Name("overclockerEnergy")
        @Config.RequiresMcRestart
        public double overclockerEnergy = 1.6;

        @Config.Comment({"The reduced processing time effected by each Overclocker Upgrade"})
        @Config.Name("overclockerTime")
        @Config.RequiresMcRestart
        public double overclockerTime = 0.7;

        @Config.Comment({"The additional energy storage provided by each Energy Storage Upgrade"})
        @Config.Name("energyStorageEnergy")
        @Config.RequiresMcRestart
        public int energyStorageEnergy = 10000;
    }

    public static class LazyAE {
        @Config.Comment({"Fixes related to the lazy ae feature may occasionally introduce errors; disabling it can prevent such issues."})
        @Config.Name("EnableRepair")
        @Config.RequiresMcRestart
        public boolean EnableRepair = true;
    }

    public static class TE5 {
        @Config.Comment({"Prevent the Thermal Expansion Cyclic Assembler from operating when the output slot contains items to avoid potential bugs"})
        @Config.Name("SequentialFabricatorMixin")
        @Config.RequiresMcRestart
        public boolean SequentialFabricatorMixin = true;

        @Config.Comment({"Whether to enable energy consumption boost, just like the original upgrade state"})
        @Config.Name("IncreasedEnergyConsumption")
        @Config.RequiresMcRestart
        public boolean IncreasedEnergyConsumption = false;

        @Config.Comment({"The maximum chance of modifying an item to be reused is 100"})
        @Config.Name("SecondaryProbability")
        @Config.RequiresMcRestart
        public boolean ReuseItemChance = true;

        @Config.Comment({"Set the efficiency of the Fuel Catalyzer, which defaults to 15"})
        @Config.Name("FuelCatalyzer")
        @Config.RequiresMcRestart
        public int FuelCatalyzer = 15;

        @Config.Comment({"Whether to enable energy consumption boost, just like the original upgrade state"})
        @Config.Name("FuelCatalyzerQuantityChanges")
        @Config.RequiresMcRestart
        public boolean FuelCatalyzerQuantityChanges = true;
    }

    public static class FTBU {
        @Config.Comment({"Change the name of the nbtedit command of FTB"})
        @Config.Name("ModifyCmdEditNBT")
        @Config.RequiresMcRestart
        public boolean ModifyCmdEditNBT = false;
    }

    public static class TF5 {
        @Config.Comment({"When the offhand has a red map and the player is crouching, cancel the clear setting"})
        @Config.Name("RedDiagramOfTheDeputy")
        @Config.RequiresMcRestart
        public boolean RedDiagramOfTheDeputy = true;
    }

    public static class Botania {
        @Config.Comment({"Fix some bugs. If there is a problem as a result, it should be closed"})
        @Config.Name("BugFix")
        @Config.RequiresMcRestart
        public boolean BugFix = true;

        @Config.Comment({"Allow flor geradora to directly link to the mana pool"})
        @Config.Name("FlowerLinkPool")
        @Config.RequiresMcRestart
        public boolean FlowerLinkPool = false;

        @Config.Comment({"If false, Spark Support will not take effect"})
        @Config.Name("SparkSupport")
        @Config.RequiresMcRestart
        public boolean SparkSupport = false;

        @Config.Comment({"Allow the use of sparks on Runic Altar"})
        @Config.Name("RuneAltarSparkSupport")
        @Config.RequiresMcRestart
        public boolean RuneAltarSparkSupport = false;

        @Config.Comment({"Allow the use of sparks on Botanical Brewery"})
        @Config.Name("BrewerySparkSupport")
        @Config.RequiresMcRestart
        public boolean BrewerySparkSupport = false;
    }

    public static class COFHCORE {
        @Config.Comment({"Modify the number of upgrades that can be placed in slots"})
        @Config.Name("SocketLimitModified")
        @Config.RequiresMcRestart
        @Config.RangeInt(min = 1, max = 64)
        public int SocketLimitModified = 1;
    }

}