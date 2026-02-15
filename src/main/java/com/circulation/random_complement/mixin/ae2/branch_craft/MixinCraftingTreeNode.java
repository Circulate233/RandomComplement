package com.circulation.random_complement.mixin.ae2.branch_craft;

import appeng.api.config.FuzzyMode;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.crafting.CraftingTreeNode;
import appeng.crafting.CraftingTreeProcess;
import appeng.crafting.MECraftingInventory;
import appeng.util.item.ItemList;
import com.circulation.random_complement.mixin.ae2.AccessorCraftingTreeProcess;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CraftingTreeNode.class, remap = false)
public abstract class MixinCraftingTreeNode {

    @Shadow
    @Final
    private IAEItemStack what;

    @WrapOperation(method = "request", at = @At(value = "INVOKE", target = "Lappeng/crafting/CraftingTreeProcess;request(Lappeng/crafting/MECraftingInventory;JLappeng/api/networking/security/IActionSource;)V", ordinal = 1))
    private void request(CraftingTreeProcess instance, MECraftingInventory iae, long o, IActionSource out, Operation<Void> original, @Local(name = "l") long l) {
        original.call(instance, iae, rc$needAmount(iae, l, (AccessorCraftingTreeProcess) instance), out);
    }

    @Unique
    private long rc$needAmount(MECraftingInventory inv, long l, AccessorCraftingTreeProcess process) {
        var outputs = this.what.copy().setStackSize(0);
        for (var output : process.getDetails().getOutputs()) {
            if (!this.what.isSameType(output)) continue;
            outputs.incStackSize(output.getStackSize());
        }
        l = (long) Math.ceil((double) l / outputs.getStackSize());
        if (process.getNodes().isEmpty()) {
            var inputs = new ItemList();
            for (var input : process.getDetails().getInputs()) {
                inputs.addStorage(input);
            }
            for (var input : inputs) {
                if (!process.getDetails().isCraftable() || !process.getDetails().canSubstitute()) {
                    var i = inv.getItemList().findPrecise(input);
                    if (i == null) return 1;
                    l = Math.min(i.getStackSize() / input.getStackSize(), l);
                } else {
                    long m = 0;
                    var list = inv.getItemList().findFuzzy(input, FuzzyMode.IGNORE_ALL);
                    for (var stack : list) {
                        m += stack.getStackSize();
                    }
                    l = Math.min(m / input.getStackSize(), l);
                }
                if (l < 2) return 1;
            }
        }
        return l;
    }
}