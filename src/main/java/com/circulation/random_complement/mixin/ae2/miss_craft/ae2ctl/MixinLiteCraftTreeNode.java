package com.circulation.random_complement.mixin.ae2.miss_craft.ae2ctl;

import com.circulation.random_complement.common.interfaces.RCCraftingJob;
import github.kasuminova.ae2ctl.common.integration.ae2.data.LiteCraftTreeNode;
import github.kasuminova.ae2ctl.mixin.ae2.AccessorCraftingTreeNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LiteCraftTreeNode.class,remap = false)
public class MixinLiteCraftTreeNode {

    @Redirect(method = "of",at = @At(value = "INVOKE", target = "Lgithub/kasuminova/ae2ctl/mixin/ae2/AccessorCraftingTreeNode;getMissing()J"))
    private static long getMissing(AccessorCraftingTreeNode instance) {
        if (!((RCCraftingJob) r$pack(instance).getJob()).canIgnoredInput()) return instance.getMissing();
        if (r$pack(instance).isCanEmit()) return instance.getMissing();
        return r$pack(instance).getHowManyEmitted();
    }

    @Unique
    private static com.circulation.random_complement.mixin.ae2.AccessorCraftingTreeNode r$pack(AccessorCraftingTreeNode node) {
        return (com.circulation.random_complement.mixin.ae2.AccessorCraftingTreeNode) node;
    }
}
