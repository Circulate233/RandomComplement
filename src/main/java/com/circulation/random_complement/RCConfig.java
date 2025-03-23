package com.circulation.random_complement;

import net.minecraftforge.common.config.Config;

@Config(modid = RandomComplement.MOD_ID, type = Config.Type.INSTANCE)
public class RCConfig {
    @Config(modid = RandomComplement.MOD_ID,category = "ae2")
    public static class AE2{
        @Config.Comment({"Disable the Build permission check for AE2's security station"})
        @Config.Name("SecurityCache")
        public static boolean SecurityCache = false;
    }

    @Config(modid = RandomComplement.MOD_ID,category = "ic2")
    public static class IC2{
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

    @Config(modid = RandomComplement.MOD_ID,category = "lazyae2")
    public static class LazyAE{
        @Config.Comment({"Fixes related to the lazy ae feature may occasionally introduce errors; disabling it can prevent such issues."})
        @Config.Name("EnableRepair")
        public static boolean EnableRepair = true;
    }

    @Config(modid = RandomComplement.MOD_ID,category = "te5")
    public static class TE5{
        @Config.Comment({"Prevent the Thermal Expansion Cyclic Assembler from operating when the output slot contains items to avoid potential bugs"})
        @Config.Name("SequentialFabricatorMixin")
        public static boolean SequentialFabricatorMixin = true;
    }
}