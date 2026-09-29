package com.circulation.random_complement.mixin.ae2;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.me.cache.CraftingGridCache;
import com.circulation.random_complement.common.crafting.RCCraftingSnapshot;
import com.circulation.random_complement.common.interfaces.RCCraftingGridCache;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = CraftingGridCache.class, remap = false)
public class MixinCraftingGridCache implements RCCraftingGridCache {

    @Unique
    private final Map<IAEItemStack, RCCraftingSnapshot.Keys> rc$trimKeys = new ConcurrentHashMap<>();
    @Unique
    private final int rc$maxCachedOutputs = 128;
    @Unique
    private final int rc$maxCachedItems = 16384;
    @Unique
    private int rc$cachedItems;
    @Unique
    private long rc$stockSizeTime = Long.MIN_VALUE;
    @Unique
    private int rc$stockSize;

    @Mutable
    @Shadow
    @Final
    private Object2ObjectMap<IAEItemStack, ImmutableList<ICraftingPatternDetails>> craftableItems;

    @Override
    public Map<IAEItemStack, ImmutableList<ICraftingPatternDetails>> rc$getCraftableItems() {
        return craftableItems;
    }

    @Override
    public RCCraftingSnapshot.Keys rc$peekTrimKeys(IAEItemStack output) {
        if (output == null) {
            return null;
        }
        return rc$trimKeys.get(output);
    }

    @Override
    public void rc$cacheTrimKeys(IAEItemStack output, RCCraftingSnapshot.Keys keys) {
        if (output == null || keys == null) {
            return;
        }
        if (rc$trimKeys.size() >= rc$maxCachedOutputs || rc$cachedItems > rc$maxCachedItems) {
            rc$trimKeys.clear();
            rc$cachedItems = 0;
        }
        rc$trimKeys.put(output, keys);
        rc$cachedItems += keys.rc$size();
    }

    @Override
    public int rc$stockSize(IItemList<IAEItemStack> full, long time) {
        if (full == null) {
            return 0;
        }
        if (rc$stockSize <= 0 || time - rc$stockSizeTime >= 20 || time < rc$stockSizeTime) {
            rc$stockSize = full.size();
            rc$stockSizeTime = time;
        }
        return rc$stockSize;
    }

    @Inject(method = "recalculateCraftingPatterns", at = @At("HEAD"))
    private void rc$dropTrimKeys(CallbackInfo ci) {
        rc$trimKeys.clear();
        rc$cachedItems = 0;
        rc$stockSize = 0;
    }
}
