package com.circulation.random_complement.mixin.ae2fc.tile;

import appeng.tile.AEBaseInvTile;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.PatternTermAutoFillPattern;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigManagerHost;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import com.circulation.random_complement.common.util.RCConfigManager;
import com.glodblock.github.common.tile.TileUltimateEncoder;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileUltimateEncoder.class)
public abstract class MixinTileUltimateEncoder extends AEBaseInvTile implements RCIConfigurableObject, RCIConfigManagerHost {

    @Unique
    private RCIConfigManager randomComplement$rcSettings;

    @Inject(method = "<init>",at = @At("TAIL"),remap = false)
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
    public void writeToNBTMixin(NBTTagCompound data, CallbackInfoReturnable<NBTTagCompound> cir) {
        this.randomComplement$rcSettings.writeToNBT(data);
    }

    @Inject(method = "readFromNBT",at = @At("TAIL"))
    public void readFromNBTMixin(NBTTagCompound data, CallbackInfo ci) {
        this.randomComplement$rcSettings.readFromNBT(data);
    }
}
