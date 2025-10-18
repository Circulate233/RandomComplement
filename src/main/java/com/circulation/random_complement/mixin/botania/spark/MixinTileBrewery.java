package com.circulation.random_complement.mixin.botania.spark;

import com.circulation.random_complement.RCConfig;
import com.google.common.base.Predicates;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.common.block.tile.TileBrewery;
import vazkii.botania.common.block.tile.TileSimpleInventory;
import vazkii.botania.common.entity.EntitySpark;

import java.util.List;

@Mixin(TileBrewery.class)
public abstract class MixinTileBrewery extends TileSimpleInventory implements IManaReceiver, ITickable, ISparkAttachable {

    @Shadow(remap = false)
    public abstract int getManaCost();

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", ordinal = 0))
    public void onUpdate(CallbackInfo ci) {
        ISparkEntity spark = this.getAttachedSpark();
        if (spark != null) {
            for (ISparkEntity otherSpark : SparkHelper.getSparksAround(this.world, (double) this.pos.getX() + (double) 0.5F, (double) this.pos.getY() + (double) 0.5F, (double) this.pos.getZ() + (double) 0.5F)) {
                if (spark != otherSpark && otherSpark.getAttachedTile() != null && otherSpark.getAttachedTile() instanceof IManaPool) {
                    otherSpark.registerTransfer(spark);
                }
            }
        }
    }

    @Unique
    @Override
    public boolean canAttachSpark(ItemStack var1) {
        return RCConfig.Botania.BrewerySparkSupport;
    }

    @Unique
    @Override
    public void attachSpark(ISparkEntity var1) {

    }

    @Unique
    @Override
    public int getAvailableSpaceForMana() {
        return Math.max(0, this.getManaCost() - getCurrentMana());
    }

    @Unique
    @Override
    public ISparkEntity getAttachedSpark() {
        List<EntitySpark> sparks = world.getEntitiesWithinAABB(EntitySpark.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)), Predicates.instanceOf(ISparkEntity.class));
        if (sparks.size() == 1)
            return sparks.get(0);
        return null;
    }

    public boolean areIncomingTranfersDone() {
        return !canRecieveManaFromBursts();
    }
}
