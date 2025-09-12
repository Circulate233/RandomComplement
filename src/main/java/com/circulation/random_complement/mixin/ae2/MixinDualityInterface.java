package com.circulation.random_complement.mixin.ae2;

import appeng.api.config.Upgrades;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.IntelligentBlocking;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigManagerHost;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import com.circulation.random_complement.common.util.RCConfigManager;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DualityInterface.class,remap = false)
public abstract class MixinDualityInterface implements RCIConfigurableObject, RCIConfigManagerHost {

    @Unique
    private RCIConfigManager randomComplement$rcSettings;

    @Shadow
    @Final
    private IInterfaceHost iHost;

    @Shadow
    public abstract int getInstalledUpgrades(Upgrades u);

    @Shadow
    protected abstract void cancelCrafting();

    @Inject(method = "<init>",at = @At("TAIL"))
    public void onInit(CallbackInfo ci){
        this.randomComplement$rcSettings = new RCConfigManager(this);
        this.randomComplement$rcSettings.registerSetting(RCSettings.IntelligentBlocking, IntelligentBlocking.CLOSE);
    }

    @Override
    public void r$updateSetting(RCIConfigManager var1, Enum<?> var2, Enum<?> var3) {
        if (this.getInstalledUpgrades(Upgrades.CRAFTING) == 0) {
            this.cancelCrafting();
        }

        this.iHost.saveChanges();
    }

    @Override
    public RCIConfigManager r$getConfigManager() {
        return randomComplement$rcSettings;
    }

    @Inject(method = "writeToNBT",at = @At("TAIL"))
    public void writeToNBTMixin(NBTTagCompound data, CallbackInfo ci) {
        this.randomComplement$rcSettings.writeToNBT(data);
    }

    @Inject(method = "readFromNBT",at = @At("TAIL"))
    public void readFromNBTMixin(NBTTagCompound data, CallbackInfo ci) {
        this.randomComplement$rcSettings.readFromNBT(data);
    }
}
