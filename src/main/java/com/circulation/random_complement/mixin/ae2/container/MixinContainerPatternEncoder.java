package com.circulation.random_complement.mixin.ae2.container;

import appeng.api.implementations.guiobjects.IGuiItemObject;
import appeng.api.storage.ITerminalHost;
import appeng.container.guisync.GuiSync;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.SlotRestrictedInput;
import appeng.helpers.IContainerCraftingPacket;
import appeng.util.Platform;
import appeng.util.inv.IAEAppEngInventory;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.PatternTermAutoFillPattern;
import com.circulation.random_complement.common.handler.MEHandler;
import com.circulation.random_complement.common.interfaces.PatternTermConfigs;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerPatternEncoder.class,remap = false)
public abstract class MixinContainerPatternEncoder extends ContainerMEMonitorable implements IAEAppEngInventory, IOptionalSlotHost, IContainerCraftingPacket, PatternTermConfigs {

    @Shadow
    protected SlotRestrictedInput patternSlotIN;

    public MixinContainerPatternEncoder(InventoryPlayer ip, ITerminalHost monitorable) {
        super(ip, monitorable);
    }

    @Unique
    @GuiSync(66)
    public PatternTermAutoFillPattern randomComplement$AutoFillPattern;

    @Inject(method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lappeng/api/storage/ITerminalHost;Z)V",at = @At("TAIL"))
    public void onInit(InventoryPlayer ip, ITerminalHost monitorable, boolean bindInventory, CallbackInfo ci){
        RCIConfigurableObject obj = (RCIConfigurableObject)monitorable;
        var cm = obj.r$getConfigManager();
        this.randomComplement$AutoFillPattern = (PatternTermAutoFillPattern)cm.getSetting(RCSettings.PatternTermAutoFillPattern);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lappeng/api/storage/ITerminalHost;Lappeng/api/implementations/guiobjects/IGuiItemObject;Z)V",at = @At("TAIL"))
    public void onInit(InventoryPlayer ip, ITerminalHost monitorable, IGuiItemObject iGuiItemObject, boolean bindInventory, CallbackInfo ci){
        RCIConfigurableObject obj = (RCIConfigurableObject)monitorable;
        var cm = obj.r$getConfigManager();
        this.randomComplement$AutoFillPattern = (PatternTermAutoFillPattern)cm.getSetting(RCSettings.PatternTermAutoFillPattern);
    }

    @Inject(method = "encodeAndMoveToInventory",at = @At("TAIL"))
    public void encodeAndMoveToInventory(CallbackInfo ci) {
        MEHandler.refillBlankPatterns(this,patternSlotIN);
    }

    @Inject(method = "encode",at = @At(value = "HEAD"))
    public void encode(CallbackInfo ci) {
        MEHandler.refillBlankPatterns(this,patternSlotIN);
    }

    @Inject(method = "detectAndSendChanges",at = @At("TAIL"),remap = true)
    public void detectAndSendChangesMixin(CallbackInfo ci) {
        if (Platform.isServer()) {
            var d = ((RCIConfigurableObject)this.getTarget());
            this.randomComplement$loadSettingsFromHost(d.r$getConfigManager());
        }
    }

    @Unique
    protected void randomComplement$loadSettingsFromHost(RCIConfigManager cm) {
        this.randomComplement$AutoFillPattern = (PatternTermAutoFillPattern) cm.getSetting(RCSettings.PatternTermAutoFillPattern);
    }

    @Unique
    @Override
    public PatternTermAutoFillPattern r$getAutoFillPattern() {
        return randomComplement$AutoFillPattern;
    }

}
