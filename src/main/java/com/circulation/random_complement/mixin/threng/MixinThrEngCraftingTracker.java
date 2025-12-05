package com.circulation.random_complement.mixin.threng;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import gnu.trove.map.TObjectIntMap;
import io.github.phantamanta44.threng.util.ThrEngCraftingTracker;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Mixin(value = ThrEngCraftingTracker.class, remap = false)
public abstract class MixinThrEngCraftingTracker {

    @Final
    @Shadow
    private Future<ICraftingJob>[] jobs;
    @Final
    @Shadow
    private ICraftingRequester owner;
    @Final
    @Shadow
    private ICraftingLink[] links;
    @Final
    @Shadow
    private TObjectIntMap<ICraftingLink> linksInv;

    @Shadow
    public abstract boolean isSlotOpen(int slot);

    /**
     * @author circulation
     * @reason 重写
     */
    @Overwrite
    public boolean requestCrafting(int slot, IAEItemStack item, World world, IGrid grid, ICraftingGrid crafting, IActionSource actionSrc) {
        if (item != null) {
            ItemStack inputStack = item.getCachedItemStack(item.getStackSize());
            ItemStack remaining = ItemStack.EMPTY;
            item.setCachedItemStack(inputStack);
            Future<ICraftingJob> jobCalculation = this.jobs[slot];
            if (!this.isSlotOpen(slot)) {
                return false;
            }

            if (jobCalculation == null && this.links[slot] == null) {
                IAEItemStack itemC = item.copy();
                jobs[slot] = crafting.beginCraftingJob(world, grid, actionSrc, itemC, null);
                jobCalculation = jobs[slot];
            }

            if (jobCalculation == null) {
                return false;
            }

            try {
                if (jobCalculation.isDone()) {
                    ICraftingJob job = jobCalculation.get();
                    if (job != null) {
                        ICraftingLink link = crafting.submitJob(job, this.owner, null, false, actionSrc);
                        this.jobs[slot] = null;
                        if (link != null) {
                            this.randomComplement$setLink(slot, link);
                            this.linksInv.put(link, slot);
                            return true;
                        }
                    }
                }
            } catch (ExecutionException | InterruptedException ignored) {

            }
        }
        return false;
    }

    @Unique
    private void randomComplement$setLink(int slot, ICraftingLink l) {
        this.links[slot] = l;
        boolean hasStuff = false;

        for (int x = 0; x < links.length; ++x) {
            ICraftingLink g = links[x];
            if (g != null && !g.isCanceled() && !g.isDone()) {
                hasStuff = true;
            } else {
                this.links[x] = null;
            }
        }
    }

    @Inject(method = "isSlotOpen", at = @At("HEAD"))
    public void isSlotOpenMixin(int i, CallbackInfoReturnable<Boolean> cir) {
        if (this.links[i] != null && (this.links[i].isCanceled() || this.links[i].isDone())) {
            this.linksInv.remove(this.links[i]);
            this.links[i] = null;
        }
    }

    /**
     * @author circulation
     * @reason 和见鬼毫无区别的导致崩溃
     */
    @Overwrite
    private void updateLinks() {

    }
}