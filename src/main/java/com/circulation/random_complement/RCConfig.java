package com.circulation.random_complement;

import net.minecraftforge.common.config.Config;

@Config(modid = RandomComplement.MOD_ID, type = Config.Type.INSTANCE)
public class RCConfig {
    @Config.Comment({"Disable the Build permission check for AE2's security station"})
    @Config.Name("SecurityCache")
    public static boolean SecurityCache = false;
}
