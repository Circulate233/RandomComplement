package com.circulation.random_complement.mixin.ae2fc.container;

import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerExpandedProcessingPatternTerm;
import appeng.container.implementations.ContainerPatternEncoder;
import com.circulation.random_complement.common.handler.MEHandler;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerExpandedProcessingPatternTerm.class,remap = false)
public abstract class MixinContainerExpandedProcessingPatternTerm extends ContainerPatternEncoder {

    protected MixinContainerExpandedProcessingPatternTerm(InventoryPlayer ip, ITerminalHost monitorable, boolean bindInventory) {
        super(ip, monitorable, bindInventory);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(InventoryPlayer ip, ITerminalHost monitorable, CallbackInfo ci) {
        MEHandler.refillBlankPatterns(this, patternSlotIN);
    }
}
