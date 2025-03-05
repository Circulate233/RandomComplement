package com.circulation.random_complement.mixin.threng;

import io.github.phantamanta44.threng.tile.TileLevelMaintainer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TileLevelMaintainer.InventoryRequest.class,remap = false)
public interface AccessorInventoryRequest {

    @Accessor("requestStacks")
    ItemStack[] getRequestStacks();

    @Accessor("requestQtys")
    long[] getRequestQtys();

    @Accessor("requestBatches")
    long[] getRequestBatches();

}
