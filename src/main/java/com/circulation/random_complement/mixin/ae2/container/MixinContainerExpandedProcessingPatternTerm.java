package com.circulation.random_complement.mixin.ae2.container;

import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerExpandedProcessingPatternTerm;
import com.circulation.random_complement.common.util.MEHandler;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerExpandedProcessingPatternTerm.class, remap = false)
public abstract class MixinContainerExpandedProcessingPatternTerm extends MixinContainerPatternEncoder {

    public MixinContainerExpandedProcessingPatternTerm(InventoryPlayer ip, ITerminalHost monitorable) {
        super(ip, monitorable);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(InventoryPlayer ip, ITerminalHost monitorable, CallbackInfo ci) {
        MEHandler.refillBlankPatterns(this, patternSlotIN);
    }
}
