package com.circulation.random_complement.mixin.threng;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.storage.IStackWatcherHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.fluids.util.AEFluidStack;
import appeng.me.GridAccessException;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.common.interfaces.AEIgnoredInputMachine;
import com.glodblock.github.common.item.fake.FakeFluids;
import com.glodblock.github.common.item.fake.FakeItemRegister;
import com.glodblock.github.integration.mek.FakeGases;
import com.mekeng.github.common.me.data.IAEGasStack;
import com.mekeng.github.common.me.data.impl.AEGasStack;
import com.mekeng.github.common.me.storage.IGasStorageChannel;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.threng.ThrEngConfig;
import io.github.phantamanta44.threng.tile.TileLevelMaintainer;
import io.github.phantamanta44.threng.tile.base.TileNetworkDevice;
import io.github.phantamanta44.threng.util.ThrEngCraftingTracker;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = TileLevelMaintainer.class, remap = false)
public abstract class MixinTileLevelMaintainer extends TileNetworkDevice implements IStackWatcherHost, ICraftingRequester, AEIgnoredInputMachine {

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

    @Unique
    private boolean r$isIgnored = false;

    /**
     * @author circulation
     * @reason 删除多余的检测
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    protected void tick(CallbackInfo ci) {
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
                                    long toCraft = this.r$computeDelta(i, knownCounts[i]);
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
     * @reason 修复插入物品逻辑，防止出现意外地吞物品
     */
    @Inject(method = "injectCraftedItems", at = @At("HEAD"), cancellable = true)
    public void injectCraftedItems(ICraftingLink link, IAEItemStack stack, Actionable mode, CallbackInfoReturnable<IAEItemStack> cir) {
        if (stack == null) {
            cir.setReturnValue(null);
        } else {
            int slot = this.crafter.getSlotForJob(link);
            if (slot == -1) {
                cir.setReturnValue(stack);
            } else if (mode == Actionable.SIMULATE) {
                if (this.aeGrid().isPresent()) {
                    var grid = this.aeGrid().get();
                    IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
                    var gridCache = ((IStorageGrid) grid.getCache(IStorageGrid.class));
                    IMEMonitor<IAEItemStack> storageGrid = gridCache.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                    if (Loader.isModLoaded("ae2fc")) {
                        var i = ae2fc$work(grid, mode, gridCache, stack);
                        cir.setReturnValue(i);
                        return;
                    } else {
                        cir.setReturnValue(Platform.poweredInsert(energyGrid, storageGrid, stack, this.actionSource, Actionable.MODULATE));
                    }
                    return;
                }
                cir.setReturnValue(null);
            } else {
                if (this.aeGrid().isPresent()) {
                    var grid = this.aeGrid().get();
                    IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
                    var gridCache = ((IStorageGrid) grid.getCache(IStorageGrid.class));
                    IMEMonitor<IAEItemStack> storageGrid = gridCache.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                    if (Loader.isModLoaded("ae2fc")) {
                        var i = ae2fc$work(grid, mode, gridCache, stack);
                        cir.setReturnValue(i);
                        return;
                    } else {
                        cir.setReturnValue(Platform.poweredInsert(energyGrid, storageGrid, stack, this.actionSource, Actionable.MODULATE));
                    }
                    return;
                }
                cir.setReturnValue(null);
            }
        }
    }

    @Unique
    @Optional.Method(modid = "ae2fc")
    private IAEItemStack ae2fc$work(IGrid grid, Actionable mode, IStorageGrid gridCache, IAEItemStack stack) {
        if (FakeFluids.isFluidFakeItem(stack.getDefinition())) {
            IMEMonitor<IAEFluidStack> fluidGrid = gridCache.getInventory(AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class));

            ItemStack inputStack = stack.getCachedItemStack(stack.getStackSize());
            FluidStack inputFluid = FakeItemRegister.getStack(inputStack);
            IAEFluidStack remaining;

            if (mode == appeng.api.config.Actionable.SIMULATE) {
                remaining = fluidGrid.injectItems(AEFluidStack.fromFluidStack(inputFluid), Actionable.SIMULATE, this.actionSource);
                stack.setCachedItemStack(inputStack);
            } else {
                remaining = fluidGrid.injectItems(AEFluidStack.fromFluidStack(inputFluid), Actionable.MODULATE, this.actionSource);
                if (remaining == null || remaining.getStackSize() <= 0L) {
                    ItemStack tmp = FakeFluids.packFluid2AEDrops(remaining) != null ? FakeFluids.packFluid2AEDrops(remaining).getDefinition() : null;
                    stack.setCachedItemStack(tmp);
                }
            }

            if (FakeFluids.packFluid2Drops(remaining != null ? remaining.getFluidStack() : null) == inputStack) {
                return stack;
            }

            return FakeFluids.packFluid2AEDrops(remaining);
        } else if (Loader.isModLoaded("mekeng") && FakeGases.isGasFakeItem(stack.getDefinition())) {
            return mekeng$work(mode, gridCache, stack);
        } else {
            IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
            IMEMonitor<IAEItemStack> storageGrid = gridCache.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            return Platform.poweredInsert(energyGrid, storageGrid, stack, this.actionSource, Actionable.MODULATE);
        }
    }

    @Unique
    @Optional.Method(modid = "mekeng")
    private IAEItemStack mekeng$work(Actionable mode, IStorageGrid gridCache, IAEItemStack stack) {
        IMEMonitor<IAEGasStack> gasGrid = gridCache.getInventory(AEApi.instance().storage().getStorageChannel(IGasStorageChannel.class));

        ItemStack inputStack = stack.getCachedItemStack(stack.getStackSize());
        GasStack inputGas = FakeItemRegister.getStack(inputStack);
        IAEGasStack remaining;

        if (mode == Actionable.SIMULATE) {
            remaining = gasGrid.injectItems(AEGasStack.of(inputGas), Actionable.SIMULATE, this.actionSource);
            stack.setCachedItemStack(inputStack);
        } else {
            remaining = gasGrid.injectItems(AEGasStack.of(inputGas), Actionable.MODULATE, this.actionSource);
            if (remaining == null || remaining.getStackSize() <= 0L) {
                ItemStack tmp = FakeGases.packGas2AEDrops(remaining) != null ? FakeGases.packGas2AEDrops(remaining).getDefinition() : null;
                stack.setCachedItemStack(tmp);
            }
        }

        if (FakeGases.packGas2Drops(remaining != null ? remaining.getGasStack() : null) == inputStack) {
            return stack;
        }

        return FakeGases.packGas2AEDrops(remaining);
    }

    @Unique
    private IAEItemStack r$request(int index, long count) {
        AEItemStack stack = Objects.requireNonNull(AEItemStack.fromItemStack(((AccessorInventoryRequest) this.requests).getRequestStacks()[index]));
        stack.setStackSize(count);
        return stack;
    }

    @Unique
    private long r$computeDelta(int index, long existing) {
        AccessorInventoryRequest t = ((AccessorInventoryRequest) this.requests);
        return !t.getRequestStacks()[index].isEmpty() && t.getRequestQtys()[index] - existing > 0L ? t.getRequestBatches()[index] : 0L;
    }

    @Override
    public boolean r$isIgnored() {
        return r$isIgnored;
    }

    @Override
    public void r$setIgnored(boolean b) {
        r$isIgnored = b;
    }
}