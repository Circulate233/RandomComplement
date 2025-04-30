package com.circulation.random_complement.mixin.ae2.part;

import appeng.parts.reporting.AbstractPartEncoder;
import appeng.parts.reporting.AbstractPartTerminal;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.PatternTermAutoFillPattern;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigManagerHost;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import com.circulation.random_complement.common.util.RCConfigManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractPartEncoder.class,remap = false)
public class MixinAbstractPartEncoder extends AbstractPartTerminal implements RCIConfigurableObject, RCIConfigManagerHost {

    @Unique
    private RCIConfigManager randomComplement$rcSettings;

    public MixinAbstractPartEncoder(ItemStack is) {
        super(is);
    }

    @Inject(method = "<init>",at = @At("TAIL"))
    public void onInit(CallbackInfo ci){
        this.randomComplement$rcSettings = new RCConfigManager(this);

        this.randomComplement$rcSettings.registerSetting(RCSettings.PatternTermAutoFillPattern, PatternTermAutoFillPattern.CLOSE);
    }

    @Unique
    @Override
    public void r$updateSetting(RCIConfigManager var1, Enum<?> var2, Enum<?> var3) {
        this.saveChanges();
    }

    @Unique
    @Override
    public RCIConfigManager r$getConfigManager() {
        return this.randomComplement$rcSettings;
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
