package com.circulation.random_complement.mixin.ae2;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.cache.CraftingGridCache;
import com.circulation.random_complement.common.interfaces.RCCraftingGridCache;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = CraftingGridCache.class, remap = false)
public class MixinCraftingGridCache implements RCCraftingGridCache {

    @Mutable
    @Shadow
    @Final
    private Object2ObjectMap<IAEItemStack, ImmutableList<ICraftingPatternDetails>> craftableItems;

    @Override
    public Map<IAEItemStack, ImmutableList<ICraftingPatternDetails>> rc$getCraftableItems() {
        return craftableItems;
    }
}