package com.circulation.random_complement.mixin.ae2.tile;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.IStorageMonitorableAccessor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.capabilities.Capabilities;
import appeng.me.helpers.MachineSource;
import appeng.tile.grid.AENetworkPowerTile;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.misc.TileInscriber;
import appeng.util.InventoryAdaptor;
import appeng.util.inv.WrapperChainedItemHandler;
import appeng.util.inv.WrapperFilteredItemHandler;
import appeng.util.inv.filter.IAEItemFilter;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.InscriberAutoOutput;
import com.circulation.random_complement.client.buttonsetting.InscriberBlockMode;
import com.circulation.random_complement.client.buttonsetting.InscriberMaxStackLimit;
import com.circulation.random_complement.common.handler.InscriberItemHandler;
import com.circulation.random_complement.common.interfaces.ItemHandlerTool;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigManagerHost;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import com.circulation.random_complement.common.interfaces.RCTileInscriber;
import com.circulation.random_complement.common.util.RCConfigManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = TileInscriber.class,remap = false)
public abstract class MixinTileInscriber extends AENetworkPowerTile implements RCIConfigurableObject,RCIConfigManagerHost, ItemHandlerTool, RCTileInscriber {

    @Unique
    private RCIConfigManager randomComplement$rcSettings;

    @Shadow
    @Final
    private AppEngInternalInventory topItemHandler;
    @Shadow
    @Final
    private AppEngInternalInventory bottomItemHandler;
    @Shadow
    @Final
    private AppEngInternalInventory sideItemHandler;

    @Unique
    final IAEItemFilter randomComplement$filter = new InscriberItemHandler((TileInscriber)(Object)this);
    @Unique
    private IItemHandler randomComplement$topItemHandlerExtern;
    @Unique
    private IItemHandler randomComplement$bottomItemHandlerExtern;
    @Unique
    private IItemHandler randomComplement$sideItemHandlerExtern;

    @Unique
    private final List<IItemHandler> randomComplement$list = new ObjectArrayList<>();

    @Unique
    private IItemHandlerModifiable randomComplement$input;

    @Unique
    private EnumMap<EnumFacing, Object> randomComplement$neighbors;

    @Unique
    private IActionSource randomComplement$mySrc;

    @Inject(method = "<init>",at = @At("TAIL"))
    public void onInit(CallbackInfo ci){
        this.randomComplement$rcSettings = new RCConfigManager(this);

        this.randomComplement$rcSettings.registerSetting(RCSettings.InscriberBlockMode, InscriberBlockMode.CLOSE);
        this.randomComplement$rcSettings.registerSetting(RCSettings.InscriberAutoOutput, InscriberAutoOutput.CLOSE);
        this.randomComplement$rcSettings.registerSetting(RCSettings.InscriberMaxStackLimit, InscriberMaxStackLimit.SMALL);

        randomComplement$topItemHandlerExtern = new WrapperFilteredItemHandler(topItemHandler,randomComplement$filter);
        randomComplement$bottomItemHandlerExtern = new WrapperFilteredItemHandler(bottomItemHandler,randomComplement$filter);
        randomComplement$sideItemHandlerExtern = new WrapperFilteredItemHandler(sideItemHandler,randomComplement$filter);

        this.randomComplement$input = new WrapperChainedItemHandler(this.randomComplement$topItemHandlerExtern, this.randomComplement$bottomItemHandlerExtern, this.randomComplement$sideItemHandlerExtern);
        this.randomComplement$list.add(topItemHandler);
        this.randomComplement$list.add(bottomItemHandler);
        this.randomComplement$list.add(sideItemHandler);

        this.randomComplement$neighbors = new EnumMap<>(EnumFacing.class);
        this.randomComplement$mySrc = new MachineSource(this);
    }

    @Unique
    @Override
    public RCIConfigManager r$getConfigManager() {
        return this.randomComplement$rcSettings;
    }

    @Inject(method = "getItemHandlerForSide",at = @At("HEAD"), cancellable = true)
    protected void getItemHandlerForSideMixin(EnumFacing facing, CallbackInfoReturnable<IItemHandler> cir) {
        if (r$getConfigManager().getSetting(RCSettings.InscriberBlockMode) == InscriberBlockMode.OPEN){
            cir.setReturnValue(this.randomComplement$input);
        }
    }

    @Unique
    @Override
    public void r$updateSetting(RCIConfigManager var1, Enum<?> var2, Enum<?> var3) {
        this.saveChanges();
    }

    @Inject(method = "writeToNBT",at = @At("TAIL"),remap = true)
    public void writeToNBTMixin(NBTTagCompound data, CallbackInfoReturnable<NBTTagCompound> cir) {
        this.randomComplement$rcSettings.writeToNBT(data);
    }

    @Inject(method = "readFromNBT",at = @At("TAIL"),remap = true)
    public void readFromNBTMixin(NBTTagCompound data, CallbackInfo ci) {
        this.randomComplement$rcSettings.readFromNBT(data);
    }

    @Redirect(method = "tickingRequest",at = @At(value = "INVOKE", target = "Lappeng/tile/inventory/AppEngInternalInventory;insertItem(ILnet/minecraft/item/ItemStack;Z)Lnet/minecraft/item/ItemStack;",ordinal = 0))
    public ItemStack tickingRequestMixin0(AppEngInternalInventory instance, int slot, ItemStack stack, boolean simulate) {
        if (!simulate && this.r$getConfigManager().getSetting(RCSettings.InscriberAutoOutput) == InscriberAutoOutput.OPEN) {
            return randomComplement$pushOut(stack);
        } else {
            return instance.insertItem(slot, stack, simulate);
        }
    }

    @Redirect(method = "tickingRequest",at = @At(value = "INVOKE", target = "Lappeng/tile/inventory/AppEngInternalInventory;setStackInSlot(ILnet/minecraft/item/ItemStack;)V"))
    public void tickingRequestMixin1(AppEngInternalInventory instance, int slot, ItemStack stack) {
        var item = instance.getStackInSlot(slot);
        if (!item.isEmpty()){
            if (item.getCount() <= 1){
                instance.setStackInSlot(slot,stack);
            } else {
                if (randomComplement$hasNamePress()) {
                    instance.setStackInSlot(slot,stack);
                } else {
                    item.setCount(item.getCount() - 1);
                }
            }
        }
    }

    @Unique
    private int randomComplement$tick = 0;

    @Inject(method = "tickingRequest",at = @At("TAIL"))
    public void tickingRequestMixin2(IGridNode node, int ticksSinceLastCall, CallbackInfoReturnable<TickRateModulation> cir) {
        if (!sideItemHandler.getStackInSlot(1).isEmpty() && this.r$getConfigManager().getSetting(RCSettings.InscriberAutoOutput) == InscriberAutoOutput.OPEN) {
            if (randomComplement$tick++ >= 20 && !sideItemHandler.getStackInSlot(1).isEmpty()) {
                var out = randomComplement$pushTickOut(sideItemHandler.getStackInSlot(1));
                this.sideItemHandler.setStackInSlot(1, out);
                randomComplement$tick = 0;
            }
        }
    }

    @Redirect(method = "getTask(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lappeng/api/features/IInscriberRecipe;",at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I",remap = true))
    private int getTaskMixin(ItemStack input, ItemStack plateA, ItemStack plateB) {
        return 1;
    }

    @Unique
    @Override
    public List<IItemHandler> r$getItemHandlers(){
        return randomComplement$list;
    }

    @Unique
    public void randomComplement$updateNeighbors() {
        for(EnumFacing f : EnumFacing.VALUES) {
            TileEntity te = this.world.getTileEntity(this.pos.offset(f));
            Object capability = null;
            if (te != null) {
                IStorageMonitorableAccessor accessor = te.getCapability(Capabilities.STORAGE_MONITORABLE_ACCESSOR, f.getOpposite());
                if (accessor != null) {
                    IStorageMonitorable inventory = accessor.getInventory(this.randomComplement$mySrc);
                    if (inventory != null) {
                        capability = inventory;
                    }
                }

                if (capability == null) {
                    capability = InventoryAdaptor.getAdaptor(te, f.getOpposite());
                }
            }

            if (capability != null) {
                this.randomComplement$neighbors.put(f, capability);
            } else {
                this.randomComplement$neighbors.remove(f);
            }
        }

    }

    @Unique
    @Override
    public void onReady() {
        super.onReady();
        this.randomComplement$updateNeighbors();
    }

    @Unique
    private ItemStack randomComplement$pushTickOut(ItemStack output) {
        if (output.isEmpty()) return output;

        final Set<EnumFacing> FILTERED_KEYS = EnumSet.of(this.getUp(),this.getUp().getOpposite());
        if (r$getConfigManager().getSetting(RCSettings.InscriberBlockMode) == InscriberBlockMode.OPEN) {
            for (Map.Entry<EnumFacing, Object> entry : randomComplement$neighbors.entrySet()) {
                EnumFacing facing = entry.getKey();
                Object capability = entry.getValue();
                output = randomComplement$pushTo(output, facing, capability);
                if (output.isEmpty()) break;
            }
        } else {
            for (Map.Entry<EnumFacing, Object> entry : randomComplement$neighbors.entrySet()) {
                EnumFacing facing = entry.getKey();
                if (FILTERED_KEYS.contains(facing))continue;
                Object capability = entry.getValue();
                output = randomComplement$pushTo(output, facing, capability);
                if (output.isEmpty()) break;
            }
        }

        return output;
    }

    @Unique
    private ItemStack randomComplement$pushOut(ItemStack output) {
        if (output.isEmpty()) return output;

        final Set<EnumFacing> FILTERED_KEYS = EnumSet.of(this.getUp(),this.getUp().getOpposite());
        if (r$getConfigManager().getSetting(RCSettings.InscriberBlockMode) == InscriberBlockMode.OPEN) {
            for (Map.Entry<EnumFacing, Object> entry : randomComplement$neighbors.entrySet()) {
                EnumFacing facing = entry.getKey();
                Object capability = entry.getValue();
                output = randomComplement$pushTo(output, facing, capability);
                if (output.isEmpty()) break;
            }
        } else {
            for (Map.Entry<EnumFacing, Object> entry : randomComplement$neighbors.entrySet()) {
                EnumFacing facing = entry.getKey();
                if (FILTERED_KEYS.contains(facing))continue;
                Object capability = entry.getValue();
                output = randomComplement$pushTo(output, facing, capability);
                if (output.isEmpty()) break;
            }
        }

        if (!output.isEmpty()) {
            return this.sideItemHandler.insertItem(1, output, false);
        } else {
            return output;
        }
    }

    @Unique
    private ItemStack randomComplement$pushTo(ItemStack output, EnumFacing facing, Object capability) {
        if (capability instanceof IStorageMonitorable monitorable) {
            IMEMonitor<IAEItemStack> monitor = monitorable.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            IAEItemStack aeStack = AEItemStack.fromItemStack(output);
            IAEItemStack remaining = monitor.injectItems(aeStack, Actionable.MODULATE, new MachineSource(this));
            return remaining != null ? remaining.createItemStack() : ItemStack.EMPTY;
        } else if (capability instanceof InventoryAdaptor adaptor) {
            ItemStack remaining = adaptor.addItems(output);
            if (remaining.getCount() < output.getCount()) {
                this.saveChanges();
            }
            return remaining;
        }
        return output;
    }

    @Unique
    private final static IItemDefinition randomComplement$namePress = AEApi.instance().definitions().materials().namePress();

    @Unique
    public boolean randomComplement$hasNamePress() {
        ItemStack slot0 = randomComplement$topItemHandlerExtern.getStackInSlot(0);
        ItemStack slot1 = randomComplement$bottomItemHandlerExtern.getStackInSlot(0);
        return randomComplement$namePress.isSameAs(slot0) || randomComplement$namePress.isSameAs(slot1);
    }

    @Override
    @Unique
    public void r$updateNeighbors(@NotNull IBlockAccess world, @NotNull BlockPos pos, @NotNull BlockPos neighbor){
        EnumFacing updateFromFacing;
        if (pos.getX() != neighbor.getX()) {
            if (pos.getX() > neighbor.getX()) {
                updateFromFacing = EnumFacing.WEST;
            } else {
                updateFromFacing = EnumFacing.EAST;
            }
        } else if (pos.getY() != neighbor.getY()) {
            if (pos.getY() > neighbor.getY()) {
                updateFromFacing = EnumFacing.DOWN;
            } else {
                updateFromFacing = EnumFacing.UP;
            }
        } else {
            if (pos.getZ() == neighbor.getZ()) {
                return;
            }

            if (pos.getZ() > neighbor.getZ()) {
                updateFromFacing = EnumFacing.NORTH;
            } else {
                updateFromFacing = EnumFacing.SOUTH;
            }
        }

        if (pos.offset(updateFromFacing).equals(neighbor)) {
            TileEntity te = this.world.getTileEntity(this.pos.offset(updateFromFacing));
            Object capability = null;
            if (te != null) {
                IStorageMonitorableAccessor accessor = te.getCapability(Capabilities.STORAGE_MONITORABLE_ACCESSOR, updateFromFacing.getOpposite());
                if (accessor != null) {
                    IStorageMonitorable inventory = accessor.getInventory(this.randomComplement$mySrc);
                    if (inventory != null) {
                        capability = inventory;
                    }
                }

                if (capability == null) {
                    capability = InventoryAdaptor.getAdaptor(te, updateFromFacing.getOpposite());
                }
            }

            if (capability != null) {
                this.randomComplement$neighbors.put(updateFromFacing, capability);
            } else {
                this.randomComplement$neighbors.remove(updateFromFacing);
            }
        }
    }
}