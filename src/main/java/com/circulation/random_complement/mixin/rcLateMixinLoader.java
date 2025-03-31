package com.circulation.random_complement.mixin;

import com.circulation.random_complement.RCConfig;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.*;
import java.util.function.BooleanSupplier;

@SuppressWarnings({"unused", "SameParameterValue"})
public class rcLateMixinLoader implements ILateMixinLoader {

    public static final Logger LOG = LogManager.getLogger("RC");
    public static final String LOG_PREFIX = "[RC]" + ' ';
    private static final Map<String, BooleanSupplier> MIXIN_CONFIGS = new LinkedHashMap<>();

    static {
        if (Loader.isModLoaded("neenergistics")){
            addMixinCFG("mixins.random_complement.nee.json");
            addModdedMixinCFG("mixins.random_complement.nee.baubles.json","baubles");
            addModdedMixinCFG("mixins.random_complement.nee.ae2e.json","ae2exttable");
        }
        if (RCConfig.LazyAE.EnableRepair) {
            addModdedMixinCFG("mixins.random_complement.threng.json", "threng");
        }
        addModdedMixinCFG("mixins.random_complement.ae2.json","appliedenergistics2");
        addModdedMixinCFG("mixins.random_complement.ae2fc.json","ae2fc");
        addModdedMixinCFG("mixins.random_complement.mmce.json","modularmachinery");
        addModdedMixinCFG("mixins.random_complement.ic2.json","ic2");
        addModdedMixinCFG("mixins.random_complement.te5.json","thermalexpansion");
    }

    @Override
    public List<String> getMixinConfigs() {
        return new ArrayList<>(MIXIN_CONFIGS.keySet());
    }

    @Override
    public boolean shouldMixinConfigQueue(final String mixinConfig) {
        BooleanSupplier supplier = MIXIN_CONFIGS.get(mixinConfig);
        if (supplier == null) {
            LOG.warn(LOG_PREFIX + "Mixin config {} is not found in config map! It will never be loaded.", mixinConfig);
            return false;
        }
        return supplier.getAsBoolean();
    }

    private static boolean modLoaded(final String modID) {
        return Loader.isModLoaded(modID);
    }

    private static void addModdedMixinCFG(final String mixinConfig, final String modID) {
        MIXIN_CONFIGS.put(mixinConfig, () -> modLoaded(modID));
    }

    private static void addModdedMixinCFG(final String mixinConfig, final String modID, final String... modIDs) {
        MIXIN_CONFIGS.put(mixinConfig, () -> modLoaded(modID) && Arrays.stream(modIDs).allMatch(Loader::isModLoaded));
    }

    private static void addMixinCFG(final String mixinConfig) {
        MIXIN_CONFIGS.put(mixinConfig, () -> true);
    }

    private static void addMixinCFG(final String mixinConfig, final BooleanSupplier conditions) {
        MIXIN_CONFIGS.put(mixinConfig, conditions);
    }
}
