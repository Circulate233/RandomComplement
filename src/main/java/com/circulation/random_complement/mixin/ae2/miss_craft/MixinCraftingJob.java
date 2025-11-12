package com.circulation.random_complement.mixin.ae2.miss_craft;

import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.storage.data.IAEItemStack;
import appeng.crafting.CraftingJob;
import appeng.crafting.CraftingTreeNode;
import appeng.crafting.MECraftingInventory;
import com.circulation.random_complement.common.interfaces.RCCraftingJob;
import com.circulation.random_complement.mixin.ae2.AccessorCraftingTreeNode;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftingJob.class, remap = false)
public abstract class MixinCraftingJob implements RCCraftingJob {

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

    @Unique
    private IAEItemStack r$wait;

    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Lappeng/crafting/MECraftingInventory;ignore(Lappeng/api/storage/data/IAEItemStack;)V", ordinal = 0))
    public void record(MECraftingInventory instance, IAEItemStack what, Operation<Void> original, @Share("rcOutput") LocalLongRef stackLocalRef) {
        var stack = instance.getItemList().findPrecise(what);
        if (stack != null) {
            var size = stack.getStackSize();
            stackLocalRef.set(size);
        } else stackLocalRef.set(0);
        original.call(instance, what);
    }

    @Unique
    private boolean r$specialDeficiency;

    @Intrinsic
    public IAEItemStack getWaitingItem() {
        return r$wait;
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lappeng/crafting/CraftingTreeNode;request(Lappeng/crafting/MECraftingInventory;JLappeng/api/networking/security/IActionSource;)Lappeng/api/storage/data/IAEItemStack;", shift = At.Shift.AFTER, ordinal = 0))
    public void supplementaryOutput(CallbackInfo ci, @Share("rcOutput") LocalLongRef stackLocalRef) {
        var tree = (AccessorCraftingTreeNode) this.tree;
        if (tree.isCanEmit()) return;
        final long out = stackLocalRef.get();
        if (out > 0) {
            for (var details : this.cc.getCraftingFor(this.output, null, 0, this.world)) {
                IAEItemStack repeatInput = this.output.copy().setStackSize(0);
                for (var input : details.getCondensedInputs()) {
                    if (this.output.equals(input)) {
                        repeatInput.incStackSize(input.getStackSize());
                    }
                }
                if (repeatInput.getStackSize() == 0) return;

                IAEItemStack repeatOutput = this.output.copy().setStackSize(0);
                for (var input : details.getCondensedOutputs()) {
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
                    r$wait = repeatInput;
                }
                break;
            }
        } else {
            for (var details : this.cc.getCraftingFor(this.output, null, 0, this.world)) {
                IAEItemStack repeatInput = this.output.copy().setStackSize(0);
                for (var input : details.getCondensedInputs()) {
                    if (this.output.equals(input)) {
                        repeatInput.incStackSize(input.getStackSize());
                    }
                }
                if (repeatInput.getStackSize() == 0) return;

                IAEItemStack repeatOutput = this.output.copy().setStackSize(0);
                for (var input : details.getCondensedOutputs()) {
                    if (this.output.equals(input)) {
                        repeatOutput.incStackSize(input.getStackSize());
                    }
                }
                if (repeatOutput.getStackSize() == 0) return;

                long outputQuantity = this.output.getStackSize() / repeatOutput.getStackSize();
                if (this.output.getStackSize() % repeatOutput.getStackSize() != 0) ++outputQuantity;

                tree.setHowManyEmitted(repeatInput.getStackSize());
                r$wait = repeatInput.setStackSize(repeatInput.getStackSize() * ++outputQuantity);
                setSpecialDeficiency(true);
            }
        }
    }

    @Intrinsic
    public boolean isSpecialDeficiency() {
        return r$specialDeficiency;
    }

    @Intrinsic
    public void setSpecialDeficiency(boolean b) {
        r$specialDeficiency = b;
    }
}