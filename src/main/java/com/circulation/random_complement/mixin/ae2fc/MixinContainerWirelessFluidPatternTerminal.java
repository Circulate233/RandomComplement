package com.circulation.random_complement.mixin.ae2fc;

import appeng.api.implementations.IUpgradeableCellContainer;
import appeng.container.implementations.ContainerWirelessPatternTerminal;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.helpers.WirelessTerminalGuiObject;
import com.circulation.random_complement.client.handler.MEHandler;
import com.glodblock.github.client.container.ContainerWirelessFluidPatternTerminal;
import com.glodblock.github.interfaces.PatternConsumer;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerWirelessFluidPatternTerminal.class,remap = false)
public abstract class MixinContainerWirelessFluidPatternTerminal extends ContainerWirelessPatternTerminal implements PatternConsumer, IUpgradeableCellContainer, IInventorySlotAware {

    public MixinContainerWirelessFluidPatternTerminal(InventoryPlayer ip, WirelessTerminalGuiObject gui) {
        super(ip, gui);
    }

    @Inject(method = "encode",at = @At(value = "INVOKE", target = "Lcom/glodblock/github/client/container/ContainerWirelessFluidPatternTerminal;encodeFluidPattern()V"))
    public void encode(CallbackInfo ci) {
        MEHandler.refillBlankPatterns(this,patternSlotIN);
    }
}
