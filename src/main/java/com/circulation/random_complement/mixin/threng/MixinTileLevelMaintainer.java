package com.circulation.random_complement.mixin.threng;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.storage.IStackWatcherHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.GridAccessException;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.RCConfig;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.threng.ThrEngConfig;
import io.github.phantamanta44.threng.tile.TileLevelMaintainer;
import io.github.phantamanta44.threng.tile.base.TileNetworkDevice;
import io.github.phantamanta44.threng.util.ThrEngCraftingTracker;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(value = TileLevelMaintainer.class, remap = false)
public abstract class MixinTileLevelMaintainer extends TileNetworkDevice implements IStackWatcherHost, ICraftingRequester {

    @Shadow
    @AutoSerialize
    @Final
    private TileLevelMaintainer.InventoryRequest requests;

    @Final
    @Shadow
    @AutoSerialize
    private ThrEngCraftingTracker crafter;

    @Final
    @Shadow
    private long[] knownCounts;

    @Shadow
    private int sleepTicks = 0;

    @Shadow
    private int sleepIncrement;

    /**
     * @author circulation
     * @reason 删除多余的检测
     */
    @Inject(method = "tick",at = @At("HEAD"), cancellable = true)
    protected void tick(CallbackInfo ci) {
        if (!RCConfig.LazyAE.EnableRepair){
            return;
        }
        if (!this.world.isRemote) {
            this.aeGrid().ifPresent((grid) -> {
                if (this.sleepTicks <= 0) {
                    IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
                    IMEMonitor<IAEItemStack> storageGrid = ((IStorageGrid) grid.getCache(IStorageGrid.class)).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                    boolean workDone = false;

                    ICraftingGrid crafting = grid.getCache(ICraftingGrid.class);

                    for (int i = 0; i < 5; ++i) {
                        if (this.requests.isRequesting(i)) {
                            IAEItemStack stack = storageGrid.getStorageList().findPrecise(AEItemStack.fromItemStack(this.requests.getStackInSlot(i)));
                            this.knownCounts[i] = stack == null ? 0L : stack.getStackSize();
                            workDone = true;

                            if (this.crafter.isSlotOpen(i)) {
                                try {
                                    long toCraft = this.novaEngineering_Core$computeDelta(i, knownCounts[i]);
                                    if (toCraft > 0 && this.crafter.requestCrafting(i, AEItemStack.fromItemStack(this.requests.getStackInSlot(i)).setStackSize(toCraft), this.world, grid, this.getProxy().getCrafting(), this.actionSource)) {
                                        workDone = true;
                                    }
                                } catch (GridAccessException ignored) {

                                }
                            }
                        }
                    }

                    if (workDone) {
                        this.setDirty();
                        if (this.sleepIncrement > ThrEngConfig.networkDevices.levelMaintainerSleepMin) {
                            this.sleepIncrement = Math.max(this.sleepIncrement - 20, ThrEngConfig.networkDevices.levelMaintainerSleepMin);
                        }
                    } else if (this.sleepIncrement < ThrEngConfig.networkDevices.levelMaintainerSleepMax) {
                        this.sleepIncrement = Math.min(this.sleepIncrement + 30, ThrEngConfig.networkDevices.levelMaintainerSleepMax);
                    }

                    this.sleepTicks = this.sleepIncrement;
                } else {
                    --this.sleepTicks;
                }

            });
        }
        ci.cancel();
    }

    /**
     * @author circulation
     * @reason 修复插入物品逻辑，防止出现意外的吞物品
     */
    @Inject(method = "injectCraftedItems", at = @At("HEAD"), cancellable = true)
    public void injectCraftedItems(ICraftingLink link, IAEItemStack stack, Actionable mode, CallbackInfoReturnable<IAEItemStack> cir) {
        if (!RCConfig.LazyAE.EnableRepair) {
            return;
        }
        if (stack == null) {
            cir.setReturnValue(null);
        } else {
            int slot = this.crafter.getSlotForJob(link);
            if (slot == -1) {
                cir.setReturnValue(stack);
            } else if (mode == Actionable.SIMULATE) {
                if (this.aeGrid().isPresent()) {
                    var grid = this.aeGrid().get();
                    IAEItemStack aeStack = Objects.requireNonNull(stack);
                    IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
                    IMEMonitor<IAEItemStack> storageGrid = ((IStorageGrid) grid.getCache(IStorageGrid.class)).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                    cir.setReturnValue(Platform.poweredInsert(energyGrid, storageGrid, aeStack, this.actionSource, mode));

                }
                cir.setReturnValue(null);
            } else {
                if (this.aeGrid().isPresent()) {
                    var grid = this.aeGrid().get();
                    IAEItemStack aeStack = Objects.requireNonNull(stack);
                    IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
                    IMEMonitor<IAEItemStack> storageGrid = ((IStorageGrid) grid.getCache(IStorageGrid.class)).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                    cir.setReturnValue(Platform.poweredInsert(energyGrid, storageGrid, aeStack, this.actionSource, Actionable.MODULATE));
                }
                cir.setReturnValue(null);
            }
        }
        cir.cancel();
    }




    @Unique
    private IAEItemStack novaEngineering_Core$request(int index, long count) {
        AEItemStack stack = Objects.requireNonNull(AEItemStack.fromItemStack(((AccessorInventoryRequest) this.requests).getRequestStacks()[index]));
        stack.setStackSize(count);
        return stack;
    }

    @Unique
    private long novaEngineering_Core$computeDelta(int index, long existing) {
        AccessorInventoryRequest t = ((AccessorInventoryRequest) this.requests);
        return !t.getRequestStacks()[index].isEmpty() && t.getRequestQtys()[index] - existing > 0L ? t.getRequestBatches()[index] : 0L;
    }

}