package com.circulation.random_complement.mixin.ae2.miss_craft;

import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.crafting.CraftingJob;
import com.circulation.random_complement.common.interfaces.AEIgnoredInputMachine;
import com.circulation.random_complement.common.interfaces.RCCraftingJob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = CraftingJob.class, remap = false)
public abstract class MixinCraftingJob implements RCCraftingJob {

    @Unique
    private boolean rc$lock = false;
    @Unique
    private IAEItemStack rc$wait;
    @Unique
    private boolean rc$specialDeficiency;
    @Unique
    private boolean rc$miss = false;
    @Shadow
    @Final
    private IActionSource actionSrc;

    @Intrinsic
    public boolean isLock() {
        return rc$lock;
    }

    @Intrinsic
    public void setLock(boolean lock) {
        this.rc$lock = lock;
    }

    @Intrinsic
    public IAEItemStack getWaitingItem() {
        return rc$wait;
    }

    @Intrinsic
    public void setWaitingItem(IAEItemStack waiting) {
        this.rc$wait = waiting;
    }

    @Intrinsic
    public boolean canIgnoredInput() {
        if (this.actionSrc.machine().orElse(null) instanceof AEIgnoredInputMachine a)
            return a.r$isIgnored();
        else return this.actionSrc.player().isPresent();
    }

    @Intrinsic
    public boolean isMiss() {
        return rc$miss;
    }

    @Intrinsic
    public void setMiss(boolean miss) {
        this.rc$miss = miss;
    }

    @Intrinsic
    public boolean isSpecialDeficiency() {
        return rc$specialDeficiency;
    }

    @Intrinsic
    public void setSpecialDeficiency(boolean b) {
        rc$specialDeficiency = b;
    }
}
