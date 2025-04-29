package com.circulation.random_complement;

import net.minecraftforge.common.config.Config;

@Config(modid = RandomComplement.MOD_ID)
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

    public static class ae2{
        @Config.Comment({"Disable the Build permission check for AE2's security station"})
        @Config.Name("SecurityCache")
        public boolean SecurityCache = false;

        @Config.Comment({"Sets the texture index used for the crafting item slot"})
        @Config.Name("CraftingSlotTextureIndex")
        @Config.RangeInt(min = 0, max = 6)
        public int craftingSlotTextureIndex = 1;
    }

    public static class IC2{
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

    public static class LazyAE{
        @Config.Comment({"Fixes related to the lazy ae feature may occasionally introduce errors; disabling it can prevent such issues."})
        @Config.Name("EnableRepair")
        @Config.RequiresMcRestart
        public boolean EnableRepair = true;
    }

    public static class TE5{
        @Config.Comment({"Prevent the Thermal Expansion Cyclic Assembler from operating when the output slot contains items to avoid potential bugs"})
        @Config.Name("SequentialFabricatorMixin")
        @Config.RequiresMcRestart
        public boolean SequentialFabricatorMixin = true;
    }

    public static class FTBU{
        @Config.Comment({"Change the name of the nbtedit command of FTB"})
        @Config.Name("ModifyCmdEditNBT")
        @Config.RequiresMcRestart
        public boolean ModifyCmdEditNBT = false;
    }

    public static class TF5{
        @Config.Comment({"When the offhand has a red map and the player is crouching, cancel the clear setting"})
        @Config.Name("RedDiagramOfTheDeputy")
        @Config.RequiresMcRestart
        public boolean RedDiagramOfTheDeputy = true;
    }

    public static class Botania{
        @Config.Comment({"If the Mana Spreader output mana is greater than the maximum value of the Mana Spreader after installing the Mana Lens, the output will be based on the maximum value of the Mana Spreader"})
        @Config.Name("ManaSpreaderFix")
        @Config.RequiresMcRestart
        public boolean ManaSpreaderFix = true;
    }
}
