package com.circulation.random_complement.mixin.ae2;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.cache.CraftingGridCache;
import com.circulation.random_complement.common.interfaces.RCCraftingGridCache;
import com.circulation.random_complement.common.util.CraftableItemMap;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Mixin(value = CraftingGridCache.class, remap = false)
public class MixinCraftingGridCache implements RCCraftingGridCache {

    @Mutable
    @Shadow
    @Final
    private Object2ObjectMap<IAEItemStack, ImmutableList<ICraftingPatternDetails>> craftableItems;

    @Shadow
    private boolean updatePatterns;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(IGrid grid, CallbackInfo ci) {
        craftableItems = new CraftableItemMap();
    }

    @Unique
    @Override
    public Collection<IAEItemStack> rc$getCanCraftableItems() {
        if (craftableItems instanceof CraftableItemMap map) {
            return map.getCanCraftableItems();
        }
        return Collections.emptySet();
    }

    @Override
    public Map<IAEItemStack, ImmutableList<ICraftingPatternDetails>> rc$getCraftableItems() {
        return craftableItems;
    }

    @Override
    public void rc$updatePatterns() {
        this.updatePatterns = true;
    }
}
