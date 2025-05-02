package com.circulation.random_complement.mixin.ae2;

import appeng.helpers.WirelessTerminalGuiObject;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.PatternTermAutoFillPattern;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigManagerHost;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import com.circulation.random_complement.common.util.RCConfigManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WirelessTerminalGuiObject.class,remap = false)
public abstract class MixinWirelessTerminalGuiObject implements RCIConfigurableObject, RCIConfigManagerHost {

    @Shadow
    @Final
    private ItemStack effectiveItem;

    @Shadow
    public abstract void saveChanges();

    @Unique
    private RCIConfigManager randomComplement$rcSettings;

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
    public void saveChanges(CallbackInfo ci) {
        NBTTagCompound data = this.effectiveItem.getTagCompound();
        this.randomComplement$rcSettings.writeToNBT(data);
    }

    @Inject(method = "loadFromNBT",at = @At("TAIL"))
    public void loadFromNBT(CallbackInfo ci) {
        this.randomComplement$rcSettings = new RCConfigManager(this);
        this.randomComplement$rcSettings.registerSetting(RCSettings.PatternTermAutoFillPattern, PatternTermAutoFillPattern.CLOSE);

        NBTTagCompound data = this.effectiveItem.getTagCompound();
        if (data != null) {
            this.randomComplement$rcSettings.readFromNBT(data);
        }
    }
}
