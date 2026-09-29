package com.circulation.random_complement.mixin.ae2;

import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.crafting.CraftingJob;
import com.circulation.random_complement.RCConfig;
import com.circulation.random_complement.common.crafting.RCCraftingSnapshot;
import com.circulation.random_complement.common.interfaces.RCAutoCraftingSource;
import com.circulation.random_complement.common.interfaces.RCCraftingGridCache;
import com.circulation.random_complement.common.interfaces.RCCraftingJob;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CraftingJob.class, remap = false)
public class MixinCraftingJob {

    @Shadow
    @Final
    private IAEItemStack output;
    @Shadow
    @Final
    private ICraftingGrid cc;
    @Shadow
    @Final
    private World world;
    @Shadow
    @Final
    private IActionSource actionSrc;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lappeng/api/storage/IMEMonitor;getStorageList()Lappeng/api/storage/data/IItemList;"))
    private IItemList<IAEItemStack> rc$trimCraftingSnapshot(IMEMonitor<IAEItemStack> monitor, Operation<IItemList<IAEItemStack>> original) {
        IItemList<IAEItemStack> full = original.call(monitor);
        if (full == null || !RCConfig.AE2.trimCraftingInventorySnapshot) {
            return full;
        }
        if (!(this.cc instanceof RCCraftingGridCache cache)) {
            return full;
        }
        boolean debug = RCConfig.AE2.debugTrimCraftingSnapshot;
        if (this.actionSrc.player().isPresent()) {
            if (!RCConfig.AE2.debugTrimPlayerSources) {
                return full;
            }
        } else {
            if (!(this.actionSrc.machine().orElse(null) instanceof RCAutoCraftingSource)) {
                if (debug) {
                    rc$log("skip source {} for {}", rc$sourceName(), this.output);
                }
                return full;
            }
            if (this instanceof RCCraftingJob job && job.canIgnoredInput()) {
                if (debug) {
                    rc$log("skip force crafting for {}", this.output);
                }
                return full;
            }
        }

        int entries = cache.rc$stockSize(full, this.world.getTotalWorldTime());
        if (entries < 1024) {
            if (debug) {
                rc$log("skip small stock {} for {}", entries, this.output);
            }
            return full;
        }

        RCCraftingSnapshot.Keys keys = cache.rc$peekTrimKeys(this.output);
        if (keys == null) {
            long start = System.nanoTime();
            keys = RCCraftingSnapshot.rc$build(this.cc, this.output, this.world);
            if (debug) {
                rc$log("build {} keys in {}us for {}", keys.rc$size(), (System.nanoTime() - start) / 1000, this.output);
            }
            cache.rc$cacheTrimKeys(this.output, keys);
        }
        if (!keys.rc$isUsable()) {
            if (debug) {
                rc$log("skip unusable keys for {}", this.output);
            }
            return full;
        }

        IItemList<IAEItemStack> trimmed = RCCraftingSnapshot.trim(full, keys, entries);
        if (debug) {
            rc$log("{} keys={} stock={} result={}", this.output, keys.rc$size(), entries,
                trimmed == full ? "full copy" : "trimmed " + trimmed.size());
        }
        return trimmed;
    }

    @Unique
    private String rc$sourceName() {
        Object machine = this.actionSrc.machine().orElse(null);
        return machine == null ? "unknown" : machine.getClass().getName();
    }

    @Unique
    private static void rc$log(String message, Object... args) {
        LogManager.getLogger("RC").info("[RC] trim " + message, args);
    }
}
