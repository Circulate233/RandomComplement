package com.circulation.random_complement.mixin.mmce;

import appeng.api.config.Actionable;
import appeng.api.config.Upgrades;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import appeng.helpers.DualityInterface;
import appeng.helpers.ICustomNameObject;
import appeng.helpers.IInterfaceHost;
import appeng.tile.inventory.AppEngInternalInventory;
import com.google.common.collect.ImmutableSet;
import github.kasuminova.mmce.common.tile.MEPatternProvider;
import github.kasuminova.mmce.common.tile.base.MEMachineComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.EnumSet;

@Mixin(value = MEPatternProvider.class,remap = false)
public abstract class MixinMEPatternProvider extends MEMachineComponent implements IInterfaceHost, ICustomNameObject {

    @Final
    @Shadow
    protected AppEngInternalInventory patterns;

    @Unique
    private String randomComplement$customName;

    @Unique
    private final DualityInterface randomComplement$duality = new DualityInterface(this.proxy,this);

    @Inject(method = "readCustomNBT",at = @At("TAIL"))
    public void readFromNBT(NBTTagCompound compound, CallbackInfo ci) {
        if (compound.hasKey("customName")) {
            this.randomComplement$customName = compound.getString("customName");
        } else {
            this.randomComplement$customName = null;
        }
    }

    @Inject(method = "writeCustomNBT",at = @At("TAIL"))
    public void writeToNBT(NBTTagCompound compound, CallbackInfo ci) {
        if (this.randomComplement$customName != null) {
            compound.setString("customName", this.randomComplement$customName);
        }
    }

    @Unique
    @Override
    public String getCustomInventoryName() {
        if (this.hasCustomInventoryName()) {
            return this.randomComplement$customName;
        } else {
            return this.getVisualItemStack().getDisplayName();
        }
    }

    @Unique
    @Override
    public boolean hasCustomInventoryName() {
        return this.randomComplement$customName != null && !this.randomComplement$customName.isEmpty();
    }

    @Unique
    @Override
    public void setCustomName(@Nullable String customName) {
        this.randomComplement$customName = customName;
    }

    @Override
    @Unique
    public DualityInterface getInterfaceDuality() {
        return randomComplement$duality;
    }

    @Override
    @Unique
    public EnumSet<EnumFacing> getTargets() {
        return EnumSet.allOf(EnumFacing.class);
    }

    @Override
    @Unique
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    @Unique
    public int getInstalledUpgrades(Upgrades upgrades) {
        return 3;
    }

    @Override
    @Unique
    public TileEntity getTile() {
        return this;
    }

    @Override
    @Unique
    public IItemHandler getInventoryByName(String name) {
        return this.randomComplement$duality.getInventoryByName(name);
    }

    @Override
    @Unique
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return this.randomComplement$duality.getRequestedJobs();
    }

    @Override
    @Unique
    public IAEItemStack injectCraftedItems(ICraftingLink link, IAEItemStack items, Actionable mode) {
        return this.randomComplement$duality.injectCraftedItems(link, items, mode);
    }

    @Override
    @Unique
    public void jobStateChange(ICraftingLink iCraftingLink) {
        this.randomComplement$duality.jobStateChange(iCraftingLink);
    }

    @Override
    @Unique
    public IConfigManager getConfigManager() {
        return this.randomComplement$duality.getConfigManager();
    }

}
