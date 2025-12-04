package com.circulation.random_complement.mixin.mmce;

import appeng.api.config.Actionable;
import appeng.api.config.Upgrades;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import com.circulation.random_complement.common.interfaces.RCIConfigManager;
import com.circulation.random_complement.common.interfaces.RCIConfigurableObject;
import com.google.common.collect.ImmutableSet;
import github.kasuminova.mmce.common.tile.MEPatternProvider;
import github.kasuminova.mmce.common.tile.base.MEMachineComponent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

@Mixin(value = MEPatternProvider.class, remap = false)
@Implements(@Interface(iface = IInterfaceHost.class, prefix = "soft$"))
public abstract class MixinMEPatternProvider extends MEMachineComponent implements RCIConfigurableObject, IInterfaceHost {

    @Unique
    private DualityInterface r$d;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        if (soft$getInterfaceDuality() == null) {
            r$d = new DualityInterface(this.proxy, this);
        }
    }

    @Intrinsic
    public DualityInterface soft$getInterfaceDuality() {
        return r$d;
    }

    @Intrinsic
    public RCIConfigManager r$getConfigManager() {
        return ((RCIConfigurableObject) soft$getInterfaceDuality()).r$getConfigManager();
    }

    @Intrinsic
    public EnumSet<EnumFacing> soft$getTargets() {
        return EnumSet.noneOf(EnumFacing.class);
    }

    @Intrinsic
    public TileEntity soft$getTileEntity() {
        return this;
    }

    @Intrinsic
    public void soft$saveChanges() {

    }

    @Intrinsic
    public int soft$getInstalledUpgrades(Upgrades upgrades) {
        return 0;
    }

    @Intrinsic
    public TileEntity soft$getTile() {
        return this;
    }

    @Intrinsic
    public IItemHandler soft$getInventoryByName(String s) {
        return null;
    }

    @Intrinsic
    public ImmutableSet<ICraftingLink> soft$getRequestedJobs() {
        return ImmutableSet.of();
    }

    @Intrinsic
    public IAEItemStack soft$injectCraftedItems(ICraftingLink iCraftingLink, IAEItemStack iaeItemStack, Actionable actionable) {
        return soft$getInterfaceDuality().injectCraftedItems(iCraftingLink, iaeItemStack, actionable);
    }

    @Intrinsic
    public void soft$jobStateChange(ICraftingLink iCraftingLink) {
        soft$getInterfaceDuality().jobStateChange(iCraftingLink);
    }

    @Intrinsic
    public IConfigManager soft$getConfigManager() {
        return soft$getInterfaceDuality().getConfigManager();
    }


}