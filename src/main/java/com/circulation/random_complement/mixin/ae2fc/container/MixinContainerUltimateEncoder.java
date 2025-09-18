package com.circulation.random_complement.mixin.ae2fc.container;

import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.parts.IPart;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.AEBaseContainer;
import appeng.container.guisync.GuiSync;
import appeng.container.implementations.ContainerWirelessTerm;
import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.SlotRestrictedInput;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.PatternTermAutoFillPattern;
import com.circulation.random_complement.common.handler.MEHandler;
import com.circulation.random_complement.common.interfaces.PatternTermConfigs;
import com.circulation.random_complement.common.interfaces.RCCraftingGridCache;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import com.circulation.random_complement.common.network.RCPacketMEInventoryUpdate;
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
import java.util.Set;

@Mixin(value = ContainerUltimateEncoder.class,remap = false)
public abstract class MixinContainerUltimateEncoder extends AEBaseContainer implements IOptionalSlotHost, PatternConsumer, PatternTermConfigs {

    public MixinContainerUltimateEncoder(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    @Unique
    @GuiSync(66)
    public PatternTermAutoFillPattern randomComplement$AutoFillPattern;

    @Final
    @Shadow
    protected SlotRestrictedInput patternSlotIN;

    @Unique
    private ContainerWirelessTerm randomComplement$containerTerminal;

    @Unique
    private boolean randomComplement$incomplete = false;

    @Shadow(remap = false)
    @Final
    private TileUltimateEncoder encoder;

    @Unique
    @Override
    public PatternTermAutoFillPattern r$getAutoFillPattern() {
        return randomComplement$AutoFillPattern;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(InventoryPlayer ipl, TileUltimateEncoder encoder, CallbackInfo ci) {
        RCIConfigurableObject obj = (RCIConfigurableObject) encoder;
        var cm = obj.r$getConfigManager();
        this.randomComplement$AutoFillPattern = (PatternTermAutoFillPattern) cm.getSetting(RCSettings.PatternTermAutoFillPattern);
        var terminalGuiObject = MEHandler.getTerminalGuiObject(ipl);
        if (terminalGuiObject != null) {
            this.randomComplement$containerTerminal = new ContainerWirelessTerm(ipl, terminalGuiObject);
            randomComplement$incomplete = true;
            if (this.randomComplement$AutoFillPattern == PatternTermAutoFillPattern.OPEN) {
                randomComplement$refillBlankPatternsUltimateEncoder(this.randomComplement$containerTerminal, patternSlotIN);
            }
        }
    }

    @Inject(method = "detectAndSendChanges",at = @At("TAIL"),remap = true)
    public void detectAndSendChangesMixin(CallbackInfo ci) {
        if (Platform.isServer()) {
            var d = (RCIConfigurableObject)this.encoder;
            this.randomComplement$loadSettingsFromHost(d.r$getConfigManager());
            if (randomComplement$incomplete) {
                var gui = MEHandler.getTerminalGuiObject(randomComplement$containerTerminal.getInventoryPlayer());
                if (gui != null) {
                    randomComplement$queueInventory(gui, (EntityPlayerMP) randomComplement$containerTerminal.getInventoryPlayer().player);
                    randomComplement$incomplete = false;
                }
            }
        }
    }

    @Unique
    protected void randomComplement$loadSettingsFromHost(RCIConfigManager cm) {
        this.randomComplement$AutoFillPattern = (PatternTermAutoFillPattern) cm.getSetting(RCSettings.PatternTermAutoFillPattern);
    }

    @Unique
    private void randomComplement$queueInventory(WirelessTerminalGuiObject w,EntityPlayerMP playerMP) {
        try {
            var piu = new RCPacketMEInventoryUpdate((short) 3);

            var node = w.getActionableNode();
            if (node == null)return;
            var grid = node.getGrid();
            if (grid == null)return;
            RCCraftingGridCache cgc = grid.getCache(ICraftingGrid.class);
            boolean isCraftable = false;
            IAEItemStack aeItem = null;
            Set<IAEItemStack> set = cgc.rc$getCraftableItems().keySet();

            if (set.isEmpty()) {
                return;
            }

            for (IAEItemStack send : set) {
                if (send.isCraftable()) {
                    try {
                        piu.appendItem(send);
                    } catch (BufferOverflowException var7) {
                        NetworkHandler.instance().sendTo(piu,playerMP);
                        piu = new RCPacketMEInventoryUpdate((short) 3);
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
    private void randomComplement$refillBlankPatternsUltimateEncoder(ContainerWirelessTerm container, SlotRestrictedInput slot) {
        if (Platform.isServer()) {
            if (this.randomComplement$AutoFillPattern == PatternTermAutoFillPattern.CLOSE)return;
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
