package com.circulation.random_complement.mixin.ae2fc.container;

import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerExpandedProcessingPatternTerm;
import com.circulation.random_complement.common.handler.MEHandler;
import com.glodblock.github.client.container.ContainerExtendedFluidPatternTerminal;
import com.glodblock.github.interfaces.PatternConsumer;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerExtendedFluidPatternTerminal.class,remap = false)
public abstract class MixinContainerFluidPatternTerminal extends ContainerExpandedProcessingPatternTerm implements PatternConsumer {

    public MixinContainerFluidPatternTerminal(InventoryPlayer ip, ITerminalHost monitorable) {
        super(ip, monitorable);
    }

    @Inject(method = "encode",at = @At(value = "HEAD"))
    public void encode(CallbackInfo ci) {
        MEHandler.refillBlankPatterns(this,patternSlotIN);
    }

}
