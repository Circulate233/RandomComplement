package com.circulation.random_complement.mixin;

import com.circulation.random_complement.common.util.VersionParser;
import hellfirepvp.modularmachinery.ModularMachinery;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.*;
import java.util.function.BooleanSupplier;

import static com.circulation.random_complement.common.util.Function.modLoaded;

public class rcLateMixinLoader implements ILateMixinLoader {

    public static final Logger LOG = LogManager.getLogger("RC");
    public static final String LOG_PREFIX = "[RC]" + ' ';
    private static final Map<String, BooleanSupplier> MIXIN_CONFIGS = new LinkedHashMap<>();

    static {
        try {
            if (modLoaded("modularmachinery")){
                MMCEInit();
            }
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
        }
        if (modLoaded("appliedenergistics2")){
            addMixinCFG("mixins.random_complement.ae2.json");
            addModdedMixinCFG("mixins.random_complement.ae2.jei.json","jei");
            if (modLoaded("neenergistics")) {
                addMixinCFG("mixins.random_complement.nee.json");
                addModdedMixinCFG("mixins.random_complement.nee.baubles.json", "baubles");
                addModdedMixinCFG("mixins.random_complement.nee.ae2e.json", "ae2exttable");
            }
        }
        addModdedMixinCFG("mixins.random_complement.threng.json", "threng");
        addModdedMixinCFG("mixins.random_complement.ae2fc.json", "ae2fc");
        addModdedMixinCFG("mixins.random_complement.ic2.json", "ic2");
        addModdedMixinCFG("mixins.random_complement.te5.json", "thermalexpansion");
        addModdedMixinCFG("mixins.random_complement.thaumicenergistics.json", "thaumicenergistics");
        addModdedMixinCFG("mixins.random_complement.ftbu.json", "ftbutilities");
        addModdedMixinCFG("mixins.random_complement.tf5.json", "thermalfoundation");
        addModdedMixinCFG("mixins.random_complement.botania.json", "botania");
        addModdedMixinCFG("mixins.random_complement.cofhcore.json", "cofhcore");
        addModdedMixinCFG("mixins.random_complement.shulkertooltip.json", "shulkertooltip");
        addModdedMixinCFG("mixins.random_complement.extendedae.json", "extendedae");
        addModdedMixinCFG("mixins.random_complement.fluxnetworks.json", "fluxnetworks");
    }

    @Optional.Method(modid = "modularmachinery")
    public static void MMCEInit() throws NoSuchFieldException, IllegalAccessException {
        var mmVersionField = (String) ModularMachinery.class.getField("VERSION").get(null);
        if (VersionParser.MinimumVersion(mmVersionField,"2.1.0")
                && !VersionParser.MinimumVersion(mmVersionField,"2.1.6")){
            addMixinCFG("mixins.random_complement.mmce.json");
            addModdedMixinCFG("mixins.random_complement.mmce.nae2.json", "nae2");
        }
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
