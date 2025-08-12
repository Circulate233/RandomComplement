package com.circulation.random_complement.mixin.ae2;

import appeng.helpers.WirelessTerminalGuiObject;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.PatternTermAutoFillPattern;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigManagerHost;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import com.circulation.random_complement.common.util.RCConfigManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WirelessTerminalGuiObject.class,remap = false)
public abstract class MixinWirelessTerminalGuiObject implements RCIConfigurableObject, RCIConfigManagerHost {

    @Shadow
    public abstract void saveChanges();

    @Unique
    private RCIConfigManager randomComplement$rcSettings;

    @Unique
    private boolean r$init = false;

    @Unique
    @Override
    public RCIConfigManager r$getConfigManager() {
        return this.randomComplement$rcSettings;
    }

    @Override
    public void r$updateSetting(RCIConfigManager var1, Enum<?> var2, Enum<?> var3) {
        this.saveChanges();
    }

    @Inject(method = "saveChanges()V",at = @At("TAIL"))
    public void saveChanges(CallbackInfo ci , @Local(name = "data") NBTTagCompound data) {
        this.randomComplement$rcSettings.writeToNBT(data);
    }

    @Inject(method = "loadFromNBT",at = @At("HEAD"), cancellable = true)
    public void loadFromNBT(CallbackInfo ci) {
        this.randomComplement$rcSettings = new RCConfigManager(this);
        this.randomComplement$rcSettings.registerSetting(RCSettings.PatternTermAutoFillPattern, PatternTermAutoFillPattern.CLOSE);
        if (Loader.isModLoaded("ae2exttable") && r$init()){
            ci.cancel();
        }
    }

    @Unique
    @Optional.Method(modid = "ae2exttable")
    private boolean r$init(){
        final boolean i = r$init;
        r$init = true;
        return i;
    }

    @Inject(method = "loadFromNBT",at = @At("TAIL"))
    public void loadFromNBT(CallbackInfo ci, @Local(name = "data") NBTTagCompound data) {
        if (data != null) {
            this.randomComplement$rcSettings.readFromNBT(data);
            RandomComplement.LOGGER.info("标记{}",data);
        }
    }
}
