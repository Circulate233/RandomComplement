package com.circulation.random_complement.mixin.thaumicenergistics;

import net.minecraft.nbt.NBTBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumicenergistics.util.ForgeUtil;

@Mixin(value = ForgeUtil.class, remap = false)
public class MixinForgeUtil {

    @Inject(method = "areNBTTagsEqual", at = @At("HEAD"), cancellable = true)
    private static void areNBTTagsEqualMixin(NBTBase a, NBTBase b, CallbackInfoReturnable<Boolean> cir) {
        if (a == null || b == null) {
            cir.setReturnValue(a == b);
        }
    }
}
