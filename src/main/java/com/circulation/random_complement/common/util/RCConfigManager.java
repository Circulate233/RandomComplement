package com.circulation.random_complement.common.util;

import appeng.api.config.LevelEmitterMode;
import appeng.api.config.StorageFilter;
import appeng.core.AELog;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigManagerHost;
import net.minecraft.nbt.NBTTagCompound;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class RCConfigManager implements RCIConfigManager {

    private final Map<RCSettings, Enum<?>> settings = new EnumMap<>(RCSettings.class);
    private final RCIConfigManagerHost target;
    private final Map<RCSettings, Enum<?>> oldSettings = new EnumMap<>(RCSettings.class);

    public RCConfigManager(RCIConfigManagerHost tile){
        this.target = tile;
    }

    @Override
    public void registerSetting(RCSettings settingName, Enum<?> defaultValue) {
        this.settings.put(settingName, defaultValue);
    }

    @Override
    public Enum<?> getSetting(RCSettings settingName) {
        Enum<?> oldValue = this.settings.get(settingName);
        if (oldValue != null) {
            return oldValue;
        } else {
            throw new IllegalStateException("Invalid Config setting. Expected a non-null value for " + settingName);
        }
    }

    public Enum<?> getOldSetting(RCSettings settingName) {
        return this.oldSettings.get(settingName);
    }

    @Override
    public Enum<?> putSetting(RCSettings settingName, Enum<?> newValue) {
        Enum<?> oldValue = this.getSetting(settingName);
        this.settings.put(settingName, newValue);
        this.oldSettings.put(settingName, oldValue);
        this.target.r$updateSetting(this, settingName, newValue);
        return oldValue;
    }

    @Override
    public Set<RCSettings> getSettings() {
        return this.settings.keySet();
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        for(Map.Entry<RCSettings, Enum<?>> entry : this.settings.entrySet()) {
            tagCompound.setString(entry.getKey().name(), this.settings.get(entry.getKey()).toString());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        for(Map.Entry<RCSettings, Enum<?>> entry : this.settings.entrySet()) {
            try {
                if (tagCompound.hasKey(entry.getKey().name())) {
                    String value = tagCompound.getString(entry.getKey().name());
                    if (value.equals("EXTACTABLE_ONLY")) {
                        value = StorageFilter.EXTRACTABLE_ONLY.toString();
                    } else if (value.equals("STOREABLE_AMOUNT")) {
                        value = LevelEmitterMode.STORABLE_AMOUNT.toString();
                    }

                    Enum<?> oldValue = this.settings.get(entry.getKey());
                    Enum<?> newValue = Enum.valueOf(oldValue.getClass(), value);
                    this.putSetting(entry.getKey(), newValue);
                }
            } catch (IllegalArgumentException e) {
                AELog.debug(e);
            }
        }

    }
}
