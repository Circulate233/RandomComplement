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
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.threng.ThrEngConfig;
import io.github.phantamanta44.threng.tile.TileLevelMaintainer;
import io.github.phantamanta44.threng.tile.base.TileNetworkDevice;
import io.github.phantamanta44.threng.util.ThrEngCraftingTracker;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(value = TileLevelMaintainer.class, remap = false)
public abstract class MixinTileLevelMaintainer extends TileNetworkDevice implements IStackWatcherHost, ICraftingRequester {

    @Shadow
    @AutoSerialize
    @Final
    private TileLevelMaintainer.InventoryRequest requests;

    @Shadow
    @AutoSerialize
    @Final
    private ThrEngCraftingTracker crafter = new ThrEngCraftingTracker(this, 5);

    @Shadow
    @Final
    private long[] knownCounts = new long[5];

    @Shadow
    private int sleepTicks = 0;

    @Shadow
    private int sleepIncrement;

    /**
     * @author circulation
     * @reason 删除多余的检测
     */
    @Overwrite
    protected void tick() {
        if (!this.world.isRemote) {
            this.aeGrid().ifPresent((grid) -> {
                if (this.sleepTicks <= 0) {
                    IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
                    IMEMonitor<IAEItemStack> storageGrid = ((IStorageGrid)grid.getCache(IStorageGrid.class)).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                    boolean workDone = false;

                    ICraftingGrid crafting = grid.getCache(ICraftingGrid.class);

                    for(int i = 0; i < 5; ++i) {
                        if (this.requests.isRequesting(i)) {
                            if (this.knownCounts[i] == -1L) {
                                IAEItemStack stack = storageGrid.getStorageList().findPrecise(AEItemStack.fromItemStack(this.requests.getStackInSlot(i)));
                                this.knownCounts[i] = stack == null ? 0L : stack.getStackSize();
                                workDone = true;
                            }

                            if (this.crafter.isSlotOpen(i)) {
                                long toCraft = novaEngineering_Core$d(i, this.knownCounts[i]);
                                if (toCraft > 0L && this.crafter.requestCrafting(i, (novaEngineering_Core$c(i, toCraft)), this.getWorld(), grid, crafting, this.actionSource)) {
                                    workDone = true;
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

    }

    /**
     * @author circulation
     * @reason 修复插入物品逻辑，防止出现意外的吞物品
     */
    @Overwrite
    @Nullable
    public IAEItemStack injectCraftedItems(ICraftingLink link, @Nullable IAEItemStack stack, Actionable mode) {
        if (stack == null) {
            return null;
        } else {
            int slot = this.crafter.getSlotForJob(link);
            if (slot == -1) {
                return stack;
            } else if (mode == Actionable.SIMULATE) {
                final IAEItemStack[] rem = new IAEItemStack[1];
                this.aeGrid().ifPresent(grid -> {
                    IAEItemStack aeStack = Objects.requireNonNull(stack);
                    IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
                    IMEMonitor<IAEItemStack> storageGrid = ((IStorageGrid)grid.getCache(IStorageGrid.class)).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

                    rem[0] = Platform.poweredInsert(energyGrid, storageGrid, aeStack, this.actionSource, Actionable.SIMULATE);
                });
                return rem[0];
            } else {
                final IAEItemStack[] rem = new IAEItemStack[1];
                this.aeGrid().ifPresent(grid -> {
                    IAEItemStack aeStack = Objects.requireNonNull(stack);
                    IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
                    IMEMonitor<IAEItemStack> storageGrid = ((IStorageGrid)grid.getCache(IStorageGrid.class)).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

                    rem[0] = Platform.poweredInsert(energyGrid, storageGrid, aeStack, this.actionSource, Actionable.MODULATE);

                    if (rem[0] == null || rem[0].getStackSize() < stack.getStackSize()) {
                        this.sleepIncrement = ThrEngConfig.networkDevices.levelMaintainerSleepMin;
                        this.sleepTicks = 0;
                    }
                });
                return rem[0];
            }
        }
    }

    @Unique
    private IAEItemStack novaEngineering_Core$c(int index, long count) {
        AEItemStack stack = Objects.requireNonNull(AEItemStack.fromItemStack(((InvokerInventoryRequest) this.requests).getRequestStacks()[index]));
        stack.setStackSize(count);
        return stack;
    }

    @Unique
    private long novaEngineering_Core$d(int index, long existing) {
        InvokerInventoryRequest t = ((InvokerInventoryRequest) this.requests);
        return !t.getRequestStacks()[index].isEmpty() && t.getRequestQtys()[index] - existing > 0L ? t.getRequestBatches()[index] : 0L;
    }

}
