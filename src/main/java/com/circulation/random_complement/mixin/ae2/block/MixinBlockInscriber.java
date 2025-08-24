package com.circulation.random_complement.mixin.ae2.block;

import appeng.block.AEBaseTileBlock;
import appeng.block.misc.BlockInscriber;
import com.circulation.random_complement.common.interfaces.RCTileInscriber;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockInscriber.class)
public abstract class MixinBlockInscriber extends AEBaseTileBlock {

    public MixinBlockInscriber(Material mat) {
        super(mat);
    }

    @Unique
    @Override
    public void onNeighborChange(@NotNull IBlockAccess world, @NotNull BlockPos pos, @NotNull BlockPos neighbor) {
        RCTileInscriber ti = this.getTileEntity(world, pos);
        if (ti != null) {
            ti.r$updateNeighbors(world, pos, neighbor);
        }
    }

}
