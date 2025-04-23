package com.circulation.random_complement.common.interfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

public interface RCTileInscriber {
    void r$updateNeighbors(@NotNull IBlockAccess world, @NotNull BlockPos pos, @NotNull BlockPos neighbor);
}
