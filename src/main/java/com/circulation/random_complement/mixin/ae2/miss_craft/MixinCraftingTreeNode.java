package com.circulation.random_complement.mixin.ae2.miss_craft;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.crafting.CraftingJob;
import appeng.crafting.CraftingTreeNode;
import appeng.crafting.CraftingTreeProcess;
import appeng.crafting.MECraftingInventory;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.common.interfaces.RCCraftingJob;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(value = CraftingTreeNode.class, remap = false)
public class MixinCraftingTreeNode {

    @Shadow
    private long howManyEmitted;

    @Shadow
    private int bytes;

    @Shadow
    @Final
    private CraftingTreeProcess parent;

    @Shadow
    @Final
    private IAEItemStack what;

    @Shadow
    private boolean canEmit;

    @Shadow
    @Final
    private ArrayList<CraftingTreeProcess> nodes;

    @Shadow
    @Final
    private CraftingJob job;

    @Inject(method = "request", at = @At(value = "INVOKE", target = "Lappeng/crafting/CraftingJob;isSimulation()Z", shift = At.Shift.BEFORE), cancellable = true)
    public void request(MECraftingInventory inv, long l, IActionSource src, CallbackInfoReturnable<IAEItemStack> cir) {
        if (!canIgnoredInput()) return;
        if (canEmit) return;
        this.bytes = (int) ((long) this.bytes + l);
        if (this.parent != null && this.what.getItem().hasContainerItem(this.what.getDefinition())) {
            ItemStack is2 = Platform.getContainerItem(this.what.copy().setStackSize(1L).createItemStack());
            IAEItemStack o = AEItemStack.fromItemStack(is2);
            if (o != null) {
                this.parent.addContainers(o);
            }
        }
        this.howManyEmitted += l;
        IAEItemStack rv = this.what.copy();
        rv.setStackSize(l);
        cir.setReturnValue(rv);
    }

    @WrapOperation(method = "setJob", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lappeng/crafting/CraftingTreeNode;howManyEmitted:J"))
    public long setJobHowManyEmitted(CraftingTreeNode instance, Operation<Long> original) {
        if (!canIgnoredInput()) return original.call(instance);
        if (canEmit) return original.call(instance);
        if (this.parent == null && ((RCCraftingJob) job).isSpecialDeficiency())
            return original.call(instance);
        if (this.what.equals(this.job.getOutput())) return 0;
        if (this.nodes.isEmpty()) return original.call(instance);
        return 0;
    }

    @WrapOperation(method = "getPlan", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lappeng/crafting/CraftingTreeNode;missing:J"))
    public long getPlanMissing(CraftingTreeNode instance, Operation<Long> original) {
        if (!canIgnoredInput()) return original.call(instance);
        if (canEmit) return original.call(instance);
        if (this.parent == null && ((RCCraftingJob) job).isSpecialDeficiency()) return howManyEmitted;
        if (this.what.equals(this.job.getOutput())) return original.call(instance);
        return this.howManyEmitted;
    }

    @WrapOperation(method = "getPlan", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lappeng/crafting/CraftingTreeNode;howManyEmitted:J"))
    public long getPlanHowManyEmitted(CraftingTreeNode instance, Operation<Long> original) {
        if (!canIgnoredInput()) return original.call(instance);
        if (canEmit) return original.call(instance);
        if (this.parent == null && ((RCCraftingJob) job).isSpecialDeficiency())
            return original.call(instance);
        if (this.what.equals(this.job.getOutput())) return 0;
        if (this.nodes.isEmpty()) return original.call(instance);
        return 0;
    }

    @Inject(method = "notRecursive", at = @At("RETURN"), cancellable = true)
    public void notRecursive(ICraftingPatternDetails details, CallbackInfoReturnable<Boolean> cir) {
        if (!canIgnoredInput()) return;
        if (canEmit) return;
        if (cir.getReturnValueZ()) {
            for (var input : details.getCondensedInputs()) {
                if (this.what.equals(input)) {
                    cir.setReturnValue(Boolean.FALSE);
                    break;
                }
            }
        }
    }

    @Intrinsic
    private boolean canIgnoredInput() {
        return ((RCCraftingJob) job).canIgnoredInput();
    }
}