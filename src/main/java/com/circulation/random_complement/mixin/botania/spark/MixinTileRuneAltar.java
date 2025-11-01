package com.circulation.random_complement.mixin.botania.spark;

import com.circulation.random_complement.RCConfig;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.common.block.tile.TileRuneAltar;
import vazkii.botania.common.block.tile.TileSimpleInventory;

import java.util.List;

@Mixin(TileRuneAltar.class)
public abstract class MixinTileRuneAltar extends TileSimpleInventory implements IManaReceiver, ITickable, ISparkAttachable {

    @Shadow(remap = false)
    public int manaToGet;

    @Shadow(remap = false)
    public abstract int getTargetMana();

    @Inject(method = "update", at = @At("HEAD"))
    public void onUpdate(CallbackInfo ci) {
        if (this.manaToGet != 0) {
            ISparkEntity spark = this.getAttachedSpark();
            if (spark != null) {
                for (ISparkEntity otherSpark : SparkHelper.getSparksAround(this.world, (double) this.pos.getX() + (double) 0.5F, (double) this.pos.getY() + (double) 0.5F, (double) this.pos.getZ() + (double) 0.5F)) {
                    if (spark != otherSpark && otherSpark.getAttachedTile() != null && otherSpark.getAttachedTile() instanceof IManaPool) {
                        otherSpark.registerTransfer(spark);
                    }
                }
            }
        }
    }

    @Intrinsic
    public boolean canAttachSpark(ItemStack var1) {
        return RCConfig.Botania.RuneAltarSparkSupport;
    }

    @Intrinsic
    public void attachSpark(ISparkEntity var1) {

    }

    @Intrinsic
    public int getAvailableSpaceForMana() {
        return Math.max(0, this.getTargetMana() - getCurrentMana());
    }

    @Intrinsic
    public ISparkEntity getAttachedSpark() {
        List<Entity> sparks = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)), Predicates.instanceOf(ISparkEntity.class));
        for (var spark : sparks) {
            if (spark instanceof ISparkEntity s) {
                return s;
            }
        }
        return null;
    }

    @Intrinsic
    public boolean areIncomingTranfersDone() {
        return !canRecieveManaFromBursts();
    }
}
