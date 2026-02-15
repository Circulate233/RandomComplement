package com.circulation.random_complement.mixin.ae2ctl.branch_craft;

import appeng.crafting.CraftingTreeProcess;
import com.circulation.random_complement.mixin.ae2.AccessorCraftingTreeProcess;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import github.kasuminova.ae2ctl.common.integration.ae2.data.LiteCraftTreeNode;
import github.kasuminova.ae2ctl.mixin.ae2.AccessorCraftingTreeNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

@Mixin(value = LiteCraftTreeNode.class, remap = false)
public class MixinLiteCraftTreeNode {

    @WrapOperation(method = "of", at = @At(value = "INVOKE", target = "Lgithub/kasuminova/ae2ctl/mixin/ae2/AccessorCraftingTreeNode;getNodes()Ljava/util/ArrayList;"))
    private static ArrayList<CraftingTreeProcess> of(AccessorCraftingTreeNode instance, Operation<ArrayList<CraftingTreeProcess>> original) {
        var list = original.call(instance);
        if (list.isEmpty() || list.size() == 1) return list;
        var f = ((AccessorCraftingTreeProcess) list.get(0)).getDetails();
        var l = ((AccessorCraftingTreeProcess) list.get(list.size() - 1)).getDetails();
        if (f == l) {
            list = new ArrayList<>(list);
            list.remove(0);
        }
        return list;
    }

}
