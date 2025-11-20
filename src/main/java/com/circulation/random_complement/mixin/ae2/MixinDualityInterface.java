package com.circulation.random_complement.mixin.ae2;

import appeng.api.config.Settings;
import appeng.api.config.Upgrades;
import appeng.api.config.YesNo;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.parts.automation.UpgradeInventory;
import appeng.util.ConfigManager;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.IntelligentBlocking;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigManagerHost;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import com.circulation.random_complement.common.util.RCConfigManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DualityInterface.class, remap = false)
public abstract class MixinDualityInterface implements RCIConfigurableObject, RCIConfigManagerHost {

    @Unique
    private RCIConfigManager randomComplement$rcSettings;

    @Shadow
    @Final
    private IInterfaceHost iHost;
    @Shadow
    @Final
    private ConfigManager cm;
    @Shadow
    @Final
    private UpgradeInventory upgrades;
    @Unique
    private int r$lastInputHash;

    @Shadow
    protected abstract void cancelCrafting();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        this.randomComplement$rcSettings = new RCConfigManager(this);
        this.randomComplement$rcSettings.registerSetting(RCSettings.IntelligentBlocking, IntelligentBlocking.CLOSE);
    }

    @Override
    public void r$updateSetting(RCIConfigManager var1, Enum<?> var2, Enum<?> var3) {
        if ((this.upgrades == null ? 0 : this.upgrades.getInstalledUpgrades(Upgrades.CRAFTING)) == 0) {
            this.cancelCrafting();
        }

        this.iHost.saveChanges();
    }

    @Override
    public RCIConfigManager r$getConfigManager() {
        return randomComplement$rcSettings;
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    public void writeToNBTMixin(NBTTagCompound data, CallbackInfo ci) {
        this.randomComplement$rcSettings.writeToNBT(data);
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    public void readFromNBTMixin(NBTTagCompound data, CallbackInfo ci) {
        this.randomComplement$rcSettings.readFromNBT(data);
    }

    @Inject(method = "isBusy", at = @At(value = "INVOKE", target = "Lappeng/helpers/DualityInterface;isBlocking()Z", shift = At.Shift.AFTER), cancellable = true)
    public void isIntelligentBlocking(CallbackInfoReturnable<Boolean> cir) {
        if (this.randomComplement$rcSettings.getSetting(RCSettings.IntelligentBlocking) == IntelligentBlocking.OPEN) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "pushPattern", at = @At("RETURN"))
    public void intelligentBlocking(ICraftingPatternDetails pattern, InventoryCrafting table, CallbackInfoReturnable<Boolean> cir) {
        if (this.cm.getSetting(Settings.BLOCK) == YesNo.YES && this.randomComplement$rcSettings.getSetting(RCSettings.IntelligentBlocking) == IntelligentBlocking.OPEN && cir.getReturnValue()) {
            this.r$lastInputHash = pattern.hashCode();
        }
    }

    @WrapOperation(method = "pushPattern", at = @At(value = "INVOKE", target = "Lappeng/helpers/DualityInterface;isBlocking()Z"))
    public boolean intelligentBlocking(DualityInterface instance, Operation<Boolean> original, @Local(name = "patternDetails") ICraftingPatternDetails patternDetails) {
        boolean b = original.call(instance);
        if (b) {
            return this.randomComplement$rcSettings
                .getSetting(RCSettings.IntelligentBlocking) != IntelligentBlocking.OPEN
                || this.r$lastInputHash != patternDetails.hashCode();
        }
        return b;
    }
}