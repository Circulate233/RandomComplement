package com.circulation.random_complement.mixin.ae2.container;

import appeng.api.implementations.IUpgradeableHost;
import appeng.container.guisync.GuiSync;
import appeng.container.implementations.ContainerInscriber;
import appeng.container.implementations.ContainerUpgradeable;
import appeng.container.slot.SlotRestrictedInput;
import appeng.tile.misc.TileInscriber;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.InscriberAutoOutput;
import com.circulation.random_complement.client.buttonsetting.InscriberBlockMode;
import com.circulation.random_complement.client.buttonsetting.InscriberMaxStackLimit;
import com.circulation.random_complement.common.interfaces.InscriberConfigs;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mixin(value = ContainerInscriber.class,remap = false)
public class MixinContainerInscriber extends ContainerUpgradeable implements InscriberConfigs {

    @Shadow
    @Final
    private TileInscriber ti;
    @Shadow
    @Final
    private Slot top;
    @Shadow
    @Final
    private Slot middle;
    @Shadow
    @Final
    private Slot bottom;

    @Unique
    @GuiSync(7)
    public InscriberBlockMode randomComplement$InscriberBlockMode;
    @Unique
    @GuiSync(8)
    public InscriberAutoOutput randomComplement$InscriberAutoOutput;
    @Unique
    @GuiSync(9)
    public InscriberMaxStackLimit randomComplement$InscriberMaxStackLimit;

    public MixinContainerInscriber(InventoryPlayer ip, IUpgradeableHost te) {
        super(ip, te);
    }

    @Inject(method = "detectAndSendChanges",at = @At(value = "INVOKE", target = "Lappeng/tile/misc/TileInscriber;getProcessingTime()I",remap = false),remap = true)
    public void detectAndSendChangesMixin(CallbackInfo ci) {
        randomComplement$loadSettingsFromHost(((RCIConfigurableObject)ti).r$getConfigManager());
    }

    @Unique
    protected void randomComplement$loadSettingsFromHost(RCIConfigManager cm) {
        this.randomComplement$InscriberBlockMode = (InscriberBlockMode) cm.getSetting(RCSettings.InscriberBlockMode);
        this.randomComplement$InscriberAutoOutput = (InscriberAutoOutput) cm.getSetting(RCSettings.InscriberAutoOutput);
        this.randomComplement$InscriberMaxStackLimit = (InscriberMaxStackLimit) cm.getSetting(RCSettings.InscriberMaxStackLimit);
        int limit = switch (this.r$getMaxStackLimit()) {
            case SMALL -> 1;
            case MEDIUM -> 4;
            case BIG -> 64;
        };
        ((SlotRestrictedInput)this.top).setStackLimit(limit);
        ((SlotRestrictedInput)this.middle).setStackLimit(limit);
        ((SlotRestrictedInput)this.bottom).setStackLimit(limit);
    }

    @Unique
    private Set<Slot> randomComplement$slotSet;

    @Inject(method = "isValidForSlot",at = @At("HEAD"))
    public void isValidForSlotMixin(Slot s, ItemStack is, CallbackInfoReturnable<Boolean> cir){
        if (randomComplement$slotSet.contains(s)) {
            int limit = switch (this.r$getMaxStackLimit()) {
                case SMALL -> 1;
                case MEDIUM -> 4;
                case BIG -> 64;
            };
            ((SlotRestrictedInput)s).setStackLimit(limit);
        }
    }

    @Inject(method = "<init>",at = @At("TAIL"))
    public void onInit(InventoryPlayer ip, TileInscriber te, CallbackInfo ci){
        RCIConfigurableObject obj = (RCIConfigurableObject)te;
        var cm = obj.r$getConfigManager();
        this.randomComplement$InscriberBlockMode = (InscriberBlockMode) cm.getSetting(RCSettings.InscriberBlockMode);
        this.randomComplement$InscriberAutoOutput = (InscriberAutoOutput) cm.getSetting(RCSettings.InscriberAutoOutput);
        this.randomComplement$InscriberMaxStackLimit = (InscriberMaxStackLimit) cm.getSetting(RCSettings.InscriberMaxStackLimit);
        randomComplement$slotSet = new HashSet<>(Arrays.asList(this.top,this.bottom,this.middle));
        int limit = switch (this.r$getMaxStackLimit()) {
            case SMALL -> 1;
            case MEDIUM -> 4;
            case BIG -> 64;
        };
        ((SlotRestrictedInput)this.top).setStackLimit(limit);
        ((SlotRestrictedInput)this.middle).setStackLimit(limit);
        ((SlotRestrictedInput)this.bottom).setStackLimit(limit);
    }

    @Unique
    @Override
    public InscriberBlockMode r$getBlockMode() {
        return randomComplement$InscriberBlockMode;
    }

    @Override
    public InscriberAutoOutput r$getAutoOutput() {
        return randomComplement$InscriberAutoOutput;
    }

    @Override
    public InscriberMaxStackLimit r$getMaxStackLimit() {
        return randomComplement$InscriberMaxStackLimit;
    }

}
