package com.circulation.random_complement.mixin.ae2.recursive;

import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.crafting.CraftingJob;
import appeng.crafting.CraftingTreeNode;
import appeng.crafting.MECraftingInventory;
import com.circulation.random_complement.RCConfig;
import com.circulation.random_complement.common.interfaces.RCRecursiveCraftingJob;
import com.circulation.random_complement.mixin.ae2.AccessorCraftingTreeNode;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(value = CraftingJob.class, remap = false)
public class MixinRecursiveCraftingJob {

    @Shadow
    @Final
    private IAEItemStack output;
    @Shadow
    private CraftingTreeNode tree;
    @Shadow
    @Final
    private ICraftingGrid cc;
    @Shadow
    @Final
    private World world;

    @SuppressWarnings("DiscouragedShift")
    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lappeng/crafting/MECraftingInventory;ignore(Lappeng/api/storage/data/IAEItemStack;)V", ordinal = 0, shift = At.Shift.BEFORE))
    public void rc$record(CallbackInfo ci, @Share("rcOutput") LocalLongRef stackLocalRef, @Local(name = "craftingInventory") MECraftingInventory craftingInventory) {
        if (!(this instanceof RCRecursiveCraftingJob)) {
            return;
        }
        var stack = craftingInventory.getItemList().findPrecise(this.output);
        if (stack != null) {
            var size = stack.getStackSize();
            stackLocalRef.set(size);
        } else stackLocalRef.set(0);
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lappeng/crafting/CraftingTreeNode;request(Lappeng/crafting/MECraftingInventory;JLappeng/api/networking/security/IActionSource;)Lappeng/api/storage/data/IAEItemStack;", shift = At.Shift.AFTER, ordinal = 0))
    public void rc$supplementaryOutput(CallbackInfo ci, @Share("rcOutput") LocalLongRef stackLocalRef) {
        if (!RCConfig.AE2.recursiveCraftingOptimization) return;
        if (!(this instanceof RCRecursiveCraftingJob)) return;
        var tree = (AccessorCraftingTreeNode) this.tree;
        if (tree.isCanEmit()) return;
        final long out = stackLocalRef.get();
        Collection<ICraftingPatternDetails> details = this.cc.getCraftingFor(this.output, null, 0, this.world);
        if (details == null) return;
        if (out > 0) {
            for (var detail : details) {
                IAEItemStack repeatInput = this.output.copy().setStackSize(0);
                for (var input : detail.getCondensedInputs()) {
                    if (this.output.equals(input)) {
                        repeatInput.incStackSize(input.getStackSize());
                    }
                }
                if (repeatInput.getStackSize() == 0) return;

                IAEItemStack repeatOutput = this.output.copy().setStackSize(0);
                for (var input : detail.getCondensedOutputs()) {
                    if (this.output.equals(input)) {
                        repeatOutput.incStackSize(input.getStackSize());
                    }
                }
                if (repeatOutput.getStackSize() == 0) return;

                long outputQuantity = this.output.getStackSize() / repeatOutput.getStackSize();
                if (this.output.getStackSize() % repeatOutput.getStackSize() != 0) ++outputQuantity;
                repeatInput.setStackSize(repeatInput.getStackSize() * outputQuantity);

                if (repeatInput.getStackSize() > 0) {
                    var size = Math.min(out, repeatInput.getStackSize());
                    tree.getUsed().add(repeatOutput.setStackSize(size));
                    repeatInput.decStackSize(size);
                    rc$setWaitingItem(repeatInput);
                }
                break;
            }
        } else {
            for (var detail : details) {
                IAEItemStack repeatInput = this.output.copy().setStackSize(0);
                for (var input : detail.getCondensedInputs()) {
                    if (this.output.equals(input)) {
                        repeatInput.incStackSize(input.getStackSize());
                    }
                }
                if (repeatInput.getStackSize() == 0) return;

                IAEItemStack repeatOutput = this.output.copy().setStackSize(0);
                for (var input : detail.getCondensedOutputs()) {
                    if (this.output.equals(input)) {
                        repeatOutput.incStackSize(input.getStackSize());
                    }
                }
                if (repeatOutput.getStackSize() == 0) return;

                long outputQuantity = this.output.getStackSize() / repeatOutput.getStackSize();
                if (this.output.getStackSize() % repeatOutput.getStackSize() != 0) ++outputQuantity;

                tree.setHowManyEmitted(repeatInput.getStackSize());
                rc$setWaitingItem(repeatInput.setStackSize(repeatInput.getStackSize() * ++outputQuantity));
                rc$setSpecialDeficiency();
                rc$setMiss();
            }
        }
    }

    @Unique
    private void rc$setWaitingItem(IAEItemStack stack) {
        ((RCRecursiveCraftingJob) this).setWaitingItem(stack);
    }

    @Unique
    private void rc$setSpecialDeficiency() {
        ((RCRecursiveCraftingJob) this).setSpecialDeficiency(true);
    }

    @Unique
    private void rc$setMiss() {
        ((RCRecursiveCraftingJob) this).setMiss(true);
    }
}
