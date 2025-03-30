package com.circulation.random_complement.mixin.mmce;

import github.kasuminova.mmce.common.tile.MEPatternProvider;
import hellfirepvp.modularmachinery.common.machine.AbstractMachine;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.tiles.base.TileEntityRestrictedTick;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(value = TileMultiblockMachineController.class,remap = false)
public abstract class MixinTileMultiblockMachineController extends TileEntityRestrictedTick {

    @Shadow
    protected DynamicMachine foundMachine;

    @Inject(method = "tryColorize",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTileEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/tileentity/TileEntity;",shift = At.Shift.BY,by = 2))
    private void tryColorize(BlockPos pos, int color, CallbackInfo ci) {
        var te = this.getWorld().getTileEntity(pos);
        if (te instanceof MEPatternProvider mep) {
            var nbt = te.getTileData();
            var machine = this.foundMachine;
            var key = machine.getRegistryName().getNamespace() + "." + machine.getRegistryName().getPath() + ".prefix";
            nbt.setString("machineNamePrefix",key);

            try {
                Field machineName = AbstractMachine.class.getDeclaredField("localizedName");
                machineName.setAccessible(true);
                var panel = (String) machineName.get(machine);
                nbt.setString("machineName",panel);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}

        }
    }

}
