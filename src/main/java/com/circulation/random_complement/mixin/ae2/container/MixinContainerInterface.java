package com.circulation.random_complement.mixin.ae2.container;

import appeng.api.implementations.IUpgradeableHost;
import appeng.container.guisync.GuiSync;
import appeng.container.implementations.ContainerInterface;
import appeng.container.implementations.ContainerUpgradeable;
import appeng.container.slot.IOptionalSlotHost;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.util.Platform;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.IntelligentBlocking;
import com.circulation.random_complement.common.interfaces.InterfaceConfigs;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import com.circulation.random_complement.common.interfaces.RCIInterfaceHostHelper;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//TODO:智能阻挡功能未完成
@Mixin(value = ContainerInterface.class, remap = false)
public class MixinContainerInterface extends ContainerUpgradeable implements IOptionalSlotHost, InterfaceConfigs, RCIInterfaceHostHelper {

    @Unique
    @GuiSync(66)
    public IntelligentBlocking r$IntelligentBlocking;
    @Shadow
    @Final
    private DualityInterface myDuality;

    public MixinContainerInterface(InventoryPlayer ip, IUpgradeableHost te) {
        super(ip, te);
    }

    @Unique
    public IntelligentBlocking r$getIntelligentBlocking() {
        return r$IntelligentBlocking;
    }

    @Inject(method = "detectAndSendChanges", at = @At("HEAD"), remap = true)
    public void detectAndSendChangesMixin(CallbackInfo ci) {
        if (Platform.isServer()) {
            this.r$IntelligentBlocking = (IntelligentBlocking) (((RCIConfigurableObject) this.myDuality).r$getConfigManager().getSetting(RCSettings.IntelligentBlocking));
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(InventoryPlayer ip, IInterfaceHost te, CallbackInfo ci) {
        RCIConfigurableObject obj = (RCIConfigurableObject) te.getInterfaceDuality();
        var cm = obj.r$getConfigManager();
        this.r$IntelligentBlocking = (IntelligentBlocking) cm.getSetting(RCSettings.IntelligentBlocking);
    }

    @Override
    public IInterfaceHost r$getTarget() {
        return (IInterfaceHost) this.getTarget();
    }
}