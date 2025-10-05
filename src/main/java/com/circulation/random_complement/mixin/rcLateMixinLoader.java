package com.circulation.random_complement.mixin;

import com.circulation.random_complement.RCConfig;
import com.circulation.random_complement.RandomComplement;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import static com.circulation.random_complement.common.util.Function.modLoaded;

public class rcLateMixinLoader implements ILateMixinLoader {

    public static final Logger LOG = LogManager.getLogger("RC");
    public static final String LOG_PREFIX = "[RC]" + ' ';
    private static final Map<String, BooleanSupplier> MIXIN_CONFIGS = new Object2ObjectLinkedOpenHashMap<>();

    static {
        ConfigManager.sync(RandomComplement.MOD_ID, Config.Type.INSTANCE);

        if (modLoaded("appliedenergistics2")) {
            addMixinCFG("mixins.random_complement.ae2.json");
            addModdedMixinCFG("mixins.random_complement.ae2.jei.json", "jei");
            addModdedMixinCFG("mixins.random_complement.ae2e.json", "ae2exttable");
            addModdedMixinCFG("mixins.random_complement.nae2.json", "nae2");
            if (modLoaded("neenergistics")) {
                addMixinCFG("mixins.random_complement.nee.json");
                addModdedMixinCFG("mixins.random_complement.nee.baubles.json", "baubles");
                addModdedMixinCFG("mixins.random_complement.nee.ae2e.json", "ae2exttable");
            }

            if (isClassPresent("github.kasuminova.mmce.common.tile.MEPatternProvider")){
                addMixinCFG("mixins.random_complement.mmce.json");
                addModdedMixinCFG("mixins.random_complement.mmce.mekeng.json","mekeng");
            }
        }
        if (modLoaded("botania")) {
            addMixinCFG("mixins.random_complement.botania.json",
                    () -> RCConfig.Botania.BugFix);
            addMixinCFG("mixins.random_complement.botania.ce.json",
                    () -> {
                        try {
                            String v = Loader.instance().getIndexedModList().get("botania").getMetadata().version;
                            return v.equals("r1.10-364.4");
                        } catch (Exception e) {
                            return false;
                        }
                    });
            addMixinCFG("mixins.random_complement.botania.flower.json",
                    () -> RCConfig.Botania.FlowerLinkPool);
            addMixinCFG("mixins.random_complement.botania.spark.json",
                    () -> RCConfig.Botania.SparkSupport);
        }
        addMixinCFG("mixins.random_complement.threng.json",
                () -> modLoaded("threng") && RCConfig.LazyAE.EnableRepair);
        addModdedMixinCFG("mixins.random_complement.ae2fc.json", "ae2fc");
        addModdedMixinCFG("mixins.random_complement.ic2.json", "ic2");
        addModdedMixinCFG("mixins.random_complement.te5.json", "thermalexpansion");
        addModdedMixinCFG("mixins.random_complement.thaumicenergistics.json", "thaumicenergistics");
        addModdedMixinCFG("mixins.random_complement.ftbu.json", "ftbutilities");
        addModdedMixinCFG("mixins.random_complement.tf5.json", "thermalfoundation");
        addModdedMixinCFG("mixins.random_complement.cofhcore.json", "cofhcore");
        addModdedMixinCFG("mixins.random_complement.shulkertooltip.json", "shulkertooltip");
        addModdedMixinCFG("mixins.random_complement.extendedae.json", "extendedae");
        addModdedMixinCFG("mixins.random_complement.fluxnetworks.json", "fluxnetworks");
        addModdedMixinCFG("mixins.random_complement.jeiu.json", "jeiutilities");
        addModdedMixinCFG("mixins.random_complement.de.json", "draconicevolution");
    }

    @Override
    public List<String> getMixinConfigs() {
        return new ObjectArrayList<>(MIXIN_CONFIGS.keySet());
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

    private static boolean isClassPresent(String className) {
        String classFilePath = className.replace('.', '/') + ".class";
        ClassLoader classLoader = rcLateMixinLoader.class.getClassLoader();
        return classLoader.getResource(classFilePath) != null;
    }
}