package com.circulation.random_complement.mixin.botania.flower;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.block.tile.mana.TileSpreader;
import vazkii.botania.common.core.handler.InternalMethodHandler;

@Mixin(value = InternalMethodHandler.class,remap = false)
public abstract class MixinInternalMethodHandler {

    @Shadow
    public abstract long getWorldElapsedTicks();

    @Inject(method = "getBindDisplayForFlowerType",at = @At("HEAD"), cancellable = true)
    public void getBindDisplayForFlowerTypeI(SubTileEntity e, CallbackInfoReturnable<ItemStack> cir) {
        if (e instanceof SubTileGenerating) {
            var pos = e.getBinding();
            if (pos != null) {
                TileEntity t = e.getWorld().getTileEntity(pos);
                if (t instanceof TileSpreader){
                    cir.setReturnValue(new ItemStack(ModBlocks.spreader));
                } else if (t instanceof TilePool){
                    cir.setReturnValue(new ItemStack(ModBlocks.pool));
                }
            } else {
                if (this.getWorldElapsedTicks() % 40 > 19) {
                    cir.setReturnValue(new ItemStack(ModBlocks.spreader));
                } else {
                    cir.setReturnValue(new ItemStack(ModBlocks.pool));
                }
            }
        }
    }
}
