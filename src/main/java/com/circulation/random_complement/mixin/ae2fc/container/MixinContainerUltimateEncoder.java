package com.circulation.random_complement.mixin.ae2fc.container;

import appeng.api.AEApi;
import appeng.api.parts.IPart;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerWirelessPatternTerminal;
import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.SlotRestrictedInput;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.common.handler.MEHandler;
import com.circulation.random_complement.common.interfaces.SpecialPacket;
import com.glodblock.github.client.container.ContainerUltimateEncoder;
import com.glodblock.github.common.tile.TileUltimateEncoder;
import com.glodblock.github.interfaces.PatternConsumer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.BufferOverflowException;

@Mixin(value = ContainerUltimateEncoder.class,remap = false)
public abstract class MixinContainerUltimateEncoder extends AEBaseContainer implements IOptionalSlotHost, PatternConsumer {

    public MixinContainerUltimateEncoder(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    @Final
    @Shadow
    protected SlotRestrictedInput patternSlotIN;

    @Unique
    private ContainerWirelessPatternTerminal randomComplement$containerTerminal;

    @Unique
    private boolean randomComplement$incomplete = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(InventoryPlayer ipl, TileUltimateEncoder encoder, CallbackInfo ci) {
        var terminalGuiObject = MEHandler.getTerminalGuiObject(ipl);
        if (terminalGuiObject != null) {
            this.randomComplement$containerTerminal = new ContainerWirelessPatternTerminal(ipl,terminalGuiObject);
            randomComplement$refillBlankPatternsUltimateEncoder(this.randomComplement$containerTerminal, patternSlotIN);
            randomComplement$incomplete = true;
        }
    }

    @Inject(method = "detectAndSendChanges",at = @At("TAIL"),remap = true)
    public void detectAndSendChangesMixin(CallbackInfo ci) {
        if (Platform.isServer() && randomComplement$incomplete) {
            var gui = MEHandler.getTerminalGuiObject(randomComplement$containerTerminal.getInventoryPlayer());
            if (gui != null) {
                randomComplement$queueInventory(gui, (EntityPlayerMP) randomComplement$containerTerminal.getInventoryPlayer().player);
                randomComplement$incomplete = false;
            }
        }
    }

    @Unique
    private void randomComplement$queueInventory(WirelessTerminalGuiObject w,EntityPlayerMP playerMP) {
        try {
            PacketMEInventoryUpdate piu = new PacketMEInventoryUpdate();
            ((SpecialPacket)piu).r$setId(1);

            for (IAEItemStack send : w.getStorageList()) {
                if (send.isCraftable()) {
                    try {
                        piu.appendItem(send);
                    } catch (BufferOverflowException var7) {
                        NetworkHandler.instance().sendTo(piu,playerMP);
                        piu = new PacketMEInventoryUpdate();
                        ((SpecialPacket)piu).r$setId(1);
                        piu.appendItem(send);
                    }
                }
            }

            NetworkHandler.instance().sendTo(piu, playerMP);
        } catch (IOException e) {
            AELog.debug(e);
        }
    }

    @Inject(method = "encodeAndMoveToInventory",at = @At("TAIL"))
    public void encodeAndMoveToInventory(CallbackInfo ci) {
        if (randomComplement$containerTerminal != null) {
            randomComplement$refillBlankPatternsUltimateEncoder(randomComplement$containerTerminal,patternSlotIN);
        }
    }

    @Inject(method = "encode",at = @At(value = "HEAD"))
    public void encode(CallbackInfo ci) {
        if (randomComplement$containerTerminal != null) {
            randomComplement$refillBlankPatternsUltimateEncoder(randomComplement$containerTerminal,patternSlotIN);
        }
    }

    /*
     * 使用了GTNH团队的AE2U的相关方法
     * https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/blob/c3cd45df5db9db256b9fb4774b2cb57fdf11e389/src/main/java/appeng/container/implementations/ContainerMEMonitorable.java#L397
     */
    @Unique
    private static void randomComplement$refillBlankPatternsUltimateEncoder(ContainerWirelessPatternTerminal container, SlotRestrictedInput slot) {
        if (Platform.isServer()) {
            ItemStack blanks = slot.getStack();
            int blanksToRefill = 64;
            blanksToRefill -= blanks.getCount();
            if (blanksToRefill <= 0) return;
            var blankPattern = AEApi.instance().definitions().materials().blankPattern().maybeStack(blanksToRefill);
            if (blankPattern.isPresent()) {
                final AEItemStack request = AEItemStack
                        .fromItemStack(blankPattern.get());
                final IAEItemStack extracted = Platform
                        .poweredExtraction(container.getPowerSource(), container.getCellInventory(), request, container.getActionSource());
                if (extracted != null) {
                    if (blanks.isEmpty()){
                        blanks = request.getDefinition().copy();
                        blanks.setCount((int) (extracted.getStackSize()));
                    } else {
                        blanks.setCount((int) (blanks.getCount() + extracted.getStackSize()));
                    }
                    slot.putStack(blanks);
                }
            }
        }
    }

}
