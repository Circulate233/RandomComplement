package com.circulation.random_complement.mixin.nee.new_patten_gui;

import appeng.helpers.ItemStackHelper;
import com.github.vfyjxf.nee.jei.PatternTransferHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PatternTransferHandler.class, remap = false)
public class MixinPatternTransferHandler {

    @Redirect(method = "packRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;writeToNBT(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;", remap = true))
    public NBTTagCompound writeBigSizeItemNBT(ItemStack instance, NBTTagCompound nbtTagCompound) {
        ItemStackHelper.stackWriteToNBT(instance, nbtTagCompound);
        return nbtTagCompound;
    }
}
