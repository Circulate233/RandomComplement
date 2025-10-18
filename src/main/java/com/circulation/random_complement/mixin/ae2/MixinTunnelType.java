package com.circulation.random_complement.mixin.ae2;

import appeng.api.config.TunnelType;
import appeng.api.definitions.IItemDefinition;
import appeng.api.definitions.IParts;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(value = TunnelType.class, remap = false)
public class MixinTunnelType {

    @Shadow
    private Supplier<ItemStack> partItemStackSupplier;
    @Shadow
    private ItemStack partItemStack;

    @Shadow
    private static Supplier<ItemStack> tryPartStack(Function<IParts, IItemDefinition> supplier) {
        return null;
    }

    @Inject(method = "getPartItemStack", at = @At("HEAD"), cancellable = true)
    public void getPartItemStack(CallbackInfoReturnable<ItemStack> cir) {
        if ((Object) this == TunnelType.FLUID) {
            if (this.partItemStackSupplier != null) {
                this.partItemStack = tryPartStack(IParts::p2PTunnelFluids).get();
                this.partItemStackSupplier = null;
            }

            cir.setReturnValue(this.partItemStack);
        }
    }
}