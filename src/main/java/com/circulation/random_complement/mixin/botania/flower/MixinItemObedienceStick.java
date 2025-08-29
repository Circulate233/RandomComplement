package com.circulation.random_complement.mixin.botania.flower;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.subtile.ISubTileContainer;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.common.core.helper.Vector3;
import vazkii.botania.common.item.ItemObedienceStick;
import vazkii.botania.common.item.ItemTwigWand;

@Mixin(value = ItemObedienceStick.class,remap = false)
public class MixinItemObedienceStick {

    @Inject(method = "applyStick", at = @At("RETURN"))
    private static void applyStick(World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local(name = "tileAt") TileEntity tileAt) {
        if (cir.getReturnValue()) {
            if (tileAt instanceof IManaPool) {
                int range = 6;
                for (BlockPos pos_ : BlockPos.getAllInBox(pos.add(-range, -range, -range), pos.add(range, range, range))) {
                    if (!(pos_.distanceSq(pos) > (range * range))) {
                        TileEntity tile = world.getTileEntity(pos_);
                        if (tile instanceof ISubTileContainer) {
                            SubTileEntity subtile = ((ISubTileContainer) tile).getSubTile();
                            if (ItemObedienceStick.Actuator.generatingActuator.actuate(subtile, tileAt)) {
                                Vector3 orig = new Vector3(pos_.getX() + 0.5, pos_.getY() + 0.5, pos_.getZ() + 0.5);
                                Vector3 end = new Vector3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                                ItemTwigWand.doParticleBeam(world, orig, end);
                            }
                        }
                    }
                }
            }
        }
    }
}
