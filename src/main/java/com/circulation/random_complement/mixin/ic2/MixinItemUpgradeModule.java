package com.circulation.random_complement.mixin.ic2;

import ic2.api.item.IItemHudInfo;
import ic2.api.upgrade.IFullUpgrade;
import ic2.api.upgrade.IUpgradableBlock;
import ic2.core.item.IHandHeldSubInventory;
import ic2.core.item.ItemMulti;
import ic2.core.item.upgrade.ItemUpgradeModule;
import ic2.core.ref.ItemName;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static com.circulation.random_complement.RCConfig.IC2;

@Mixin(value = ItemUpgradeModule.class,remap = false)
public abstract class MixinItemUpgradeModule extends ItemMulti<ItemUpgradeModule.UpgradeType> implements IFullUpgrade, IHandHeldSubInventory, IItemHudInfo {

    protected MixinItemUpgradeModule(ItemName name, Class<ItemUpgradeModule.UpgradeType> typeClass) {
        super(name, typeClass);
    }

    /**
     * @author circulation
     * @reason 允许配置超频升级的效果
     */
    @Overwrite
    public double getProcessTimeMultiplier(ItemStack stack, IUpgradableBlock parent) {
        ItemUpgradeModule.UpgradeType type = this.getType(stack);
        if (type == ItemUpgradeModule.UpgradeType.overclocker){
            return IC2.overclockerTime;
        }
        return 1.0d;
    }

    /**
     * @author circulation
     * @reason 允许配置超频升级的效果
     */
    @Overwrite
    public double getEnergyDemandMultiplier(ItemStack stack, IUpgradableBlock parent) {
        ItemUpgradeModule.UpgradeType type = this.getType(stack);
        if (type == ItemUpgradeModule.UpgradeType.overclocker) {
            return IC2.overclockerEnergy;
        }
        return 1.0d;
    }

    /**
     * @author circulation
     * @reason 允许配置储能的效果
     */
    @Overwrite
    public int getExtraEnergyStorage(ItemStack stack, IUpgradableBlock parent) {
        ItemUpgradeModule.UpgradeType type = this.getType(stack);
        if (type == ItemUpgradeModule.UpgradeType.energy_storage) {
            return IC2.energyStorageEnergy;
        }
        return 0;
    }
}
