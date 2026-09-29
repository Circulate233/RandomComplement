package com.circulation.random_complement.common.integration.ae2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.function.Predicate;

public class AEIntegrationImpl implements AEIntegration {

    private static final Logger LOG = LogManager.getLogger("RC");
    private static final String LOG_PREFIX = "[RC] ";

    private final boolean enabled;
    private final boolean ae2fcEnabled;
    private final boolean ae2ExtTableEnabled;
    private final boolean nae2Enabled;
    private final boolean extendedAeEnabled;
    private final boolean neeEnabled;
    private final boolean thaumicEnergisticsEnabled;
    private final boolean thrEngEnabled;

    public AEIntegrationImpl(Predicate<String> modLoaded, boolean enableConfig) {
        Predicate<String> loadedPredicate = Objects.requireNonNull(modLoaded, "modLoaded");
        boolean ae2Loaded = loadedPredicate.test("appliedenergistics2") && !loadedPredicate.test("ae2");
        this.enabled = ae2Loaded && enableConfig;
        this.ae2fcEnabled = this.enabled && loadedPredicate.test("ae2fc");
        this.ae2ExtTableEnabled = this.enabled && loadedPredicate.test("ae2exttable");
        this.nae2Enabled = this.enabled && loadedPredicate.test("nae2");
        this.extendedAeEnabled = this.enabled && loadedPredicate.test("extendedae");
        this.neeEnabled = this.enabled && loadedPredicate.test("neenergistics");
        this.thaumicEnergisticsEnabled = this.enabled && loadedPredicate.test("thaumicenergistics");
        this.thrEngEnabled = this.enabled && loadedPredicate.test("threng");
        logState(ae2Loaded, enableConfig);
    }

    private void logState(boolean ae2Loaded, boolean enableConfig) {
        if (this.enabled) {
            LOG.info(
                LOG_PREFIX + "AE integration enabled. ae2fc={}, ae2exttable={}, nae2={}, extendedae={}, nee={}, thaumicenergistics={}, threng={}",
                this.ae2fcEnabled,
                this.ae2ExtTableEnabled,
                this.nae2Enabled,
                this.extendedAeEnabled,
                this.neeEnabled,
                this.thaumicEnergisticsEnabled,
                this.thrEngEnabled
            );
            return;
        }
        if (!ae2Loaded) {
            LOG.info(LOG_PREFIX + "AE integration disabled because appliedenergistics2 is missing.");
            return;
        }
        if (!enableConfig) {
            LOG.info(LOG_PREFIX + "AE integration disabled by config.");
            return;
        }
        throw new IllegalStateException("Unexpected AE integration state");
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isAe2fcEnabled() {
        return this.ae2fcEnabled;
    }

    @Override
    public boolean isAe2ExtTableEnabled() {
        return this.ae2ExtTableEnabled;
    }

    @Override
    public boolean isNae2Enabled() {
        return this.nae2Enabled;
    }

    @Override
    public boolean isExtendedAeEnabled() {
        return this.extendedAeEnabled;
    }

    @Override
    public boolean isNeeEnabled() {
        return this.neeEnabled;
    }

    @Override
    public boolean isThaumicEnergisticsEnabled() {
        return this.thaumicEnergisticsEnabled;
    }

    @Override
    public boolean isThrEngEnabled() {
        return this.thrEngEnabled;
    }
}
