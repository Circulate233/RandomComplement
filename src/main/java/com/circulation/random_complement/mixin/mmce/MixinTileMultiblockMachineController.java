package com.circulation.random_complement.mixin.mmce;

import com.circulation.random_complement.common.interfaces.SpecialMEPatternProvider;
import hellfirepvp.modularmachinery.common.machine.AbstractMachine;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.tiles.base.TileEntityRestrictedTick;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(value = TileMultiblockMachineController.class,remap = false)
public abstract class MixinTileMultiblockMachineController extends TileEntityRestrictedTick {

    @Shadow
    protected DynamicMachine foundMachine;

    @Unique
    private static Field randomComplement$machineField;

    static {
        try {
            randomComplement$machineField = AbstractMachine.class.getDeclaredField("localizedName");
            randomComplement$machineField.setAccessible(true);
        } catch (NoSuchFieldException ignored) {

        }
    }

    @Inject(method = "tryColorize",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTileEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/tileentity/TileEntity;",shift = At.Shift.BY,by = 2,remap = true))
    private void tryColorize(BlockPos pos, int color, CallbackInfo ci) {
        var te = this.getWorld().getTileEntity(pos);
        if (te instanceof SpecialMEPatternProvider mep) {
            var machine = this.foundMachine;
            var registry = machine.getRegistryName();
            var localizationKey = registry.getNamespace() + "." + registry.getPath();
            if (I18n.canTranslate(localizationKey)) {
                mep.r$setMachineName(localizationKey);
            } else {
                try {
                    var name = (String) randomComplement$machineField.get(machine);
                    mep.r$setMachineName(name.replaceAll("§.", "").replaceAll("#([A-Fa-f0-9]{3,6}(?:-[A-Fa-f0-9]{3,6})*)", ""));
                } catch (IllegalAccessException ignored) {

                }
            }
        }
    }

}
