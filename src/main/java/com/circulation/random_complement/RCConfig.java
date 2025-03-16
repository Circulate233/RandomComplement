package com.circulation.random_complement;

import net.minecraftforge.common.config.Config;

@Config(modid = RandomComplement.MOD_ID, type = Config.Type.INSTANCE)
public class RCConfig {
    @Config.Comment({"Disable the Build permission check for AE2's security station"})
    @Config.Name("SecurityCache")
    public static boolean SecurityCache = false;

    @Config.Comment({"The increased energy consumption caused by each Overclocker Upgrade"})
    @Config.Name("overclockerEnergy")
    public static double overclockerEnergy = 1.6;

    @Config.Comment({"The reduced processing time effected by each Overclocker Upgrade"})
    @Config.Name("overclockerTime")
    public static double overclockerTime = 0.7;

    @Config.Comment({"The additional energy storage provided by each Energy Storage Upgrade"})
    @Config.Name("energyStorageEnergy")
    public static int energyStorageEnergy = 10000;
}