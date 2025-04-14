package com.circulation.random_complement.common.interfaces;

import com.circulation.random_complement.client.RCSettings;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Set;

public interface RCIConfigManager {
    Set<RCSettings> getSettings();

    void registerSetting(RCSettings var1, Enum<?> var2);

    Enum<?> getSetting(RCSettings var1);

    Enum<?> putSetting(RCSettings var1, Enum<?> var2);

    void writeToNBT(NBTTagCompound var1);

    void readFromNBT(NBTTagCompound var1);
}
