package com.circulation.random_complement.mixin.ae2;

import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.SlotRestrictedInput;
import appeng.helpers.IContainerCraftingPacket;
import appeng.util.inv.IAEAppEngInventory;
import com.circulation.random_complement.client.handler.MEHandler;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerPatternEncoder.class,remap = false)
public abstract class MixinContainerPatternEncoder extends ContainerMEMonitorable implements IAEAppEngInventory, IOptionalSlotHost, IContainerCraftingPacket {

    @Shadow
    protected SlotRestrictedInput patternSlotIN;

    public MixinContainerPatternEncoder(InventoryPlayer ip, ITerminalHost monitorable) {
        super(ip, monitorable);
    }

    @Inject(method = "encodeAndMoveToInventory",at = @At("TAIL"))
    public void encodeAndMoveToInventory(CallbackInfo ci) {
        MEHandler.refillBlankPatterns(this,patternSlotIN);
    }

    @Inject(method = "encode",at = @At(value = "HEAD"))
    public void encode(CallbackInfo ci) {
        MEHandler.refillBlankPatterns(this,patternSlotIN);
    }

}
