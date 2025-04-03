package com.circulation.random_complement.mixin.mmce;

import com.circulation.random_complement.common.interfaces.SpecialMEPatternProvider;
import hellfirepvp.modularmachinery.common.machine.AbstractMachine;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.tiles.base.TileEntityRestrictedTick;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileMultiblockMachineController.class,remap = false)
public abstract class MixinTileMultiblockMachineController extends TileEntityRestrictedTick {

    @Shadow
    protected DynamicMachine foundMachine;

    @Inject(method = "tryColorize",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTileEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/tileentity/TileEntity;",shift = At.Shift.BY,by = 2,remap = true))
    private void tryColorize(BlockPos pos, int color, CallbackInfo ci) {
        var te = this.getWorld().getTileEntity(pos);
        if (te instanceof SpecialMEPatternProvider mep) {
            var machine = (AccessorAbstractMachine) this.foundMachine;
            var panel = machine.getLocalizedName();
            mep.r$setMachineName(panel.replaceAll("§.","").replaceAll("#([A-Fa-f0-9]{3,6}(?:-[A-Fa-f0-9]{3,6})*)",""));
        }
    }

    @Mixin(value = AbstractMachine.class,remap = false)
    public interface AccessorAbstractMachine{

        @Accessor
        String getLocalizedName();

    }

}
