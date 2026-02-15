package com.circulation.random_complement.mixin.ae2ctl.miss_craft;

import com.circulation.random_complement.common.interfaces.RCCraftingJob;
import github.kasuminova.ae2ctl.common.integration.ae2.data.LiteCraftTreeNode;
import github.kasuminova.ae2ctl.mixin.ae2.AccessorCraftingTreeNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LiteCraftTreeNode.class,remap = false)
public class MixinLiteCraftTreeNode {

    @Redirect(method = "of",at = @At(value = "INVOKE", target = "Lgithub/kasuminova/ae2ctl/mixin/ae2/AccessorCraftingTreeNode;getMissing()J"))
    private static long getMissing(AccessorCraftingTreeNode instance) {
        var ii = (com.circulation.random_complement.mixin.ae2.AccessorCraftingTreeNode) instance;
        if (!((RCCraftingJob) ii.getJob()).canIgnoredInput()) return instance.getMissing();
        if (ii.isCanEmit()) return instance.getMissing();
        return ii.getHowManyEmitted();
    }

}
