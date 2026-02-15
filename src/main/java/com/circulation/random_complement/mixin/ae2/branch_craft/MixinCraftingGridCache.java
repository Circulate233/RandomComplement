package com.circulation.random_complement.mixin.ae2.branch_craft;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.me.cache.CraftingGridCache;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Mixin(value = CraftingGridCache.class, remap = false)
public abstract class MixinCraftingGridCache {

    @Shadow
    @Final
    private static Comparator<ICraftingPatternDetails> COMPARATOR;

    @Redirect(method = "recalculateCraftingPatterns", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2ObjectMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    private Object recalculateCraftingPatterns(Object2ObjectMap<Object, Object> instance, Object o, Object i, @Share("rc$map") LocalRef<ObjectSet<Object>> ref) {
        ref.set(new ObjectOpenHashSet<>());
        return instance.put(o, ref.get());
    }

    @WrapOperation(method = "recalculateCraftingPatterns", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;copyOf(Ljava/util/Collection;)Lcom/google/common/collect/ImmutableList;"))
    private ImmutableList<ICraftingPatternDetails> recalculateCraftingPatterns(Collection<ICraftingPatternDetails> c, Operation<ImmutableList<ICraftingPatternDetails>> original) {
        var list = c.stream().sorted(COMPARATOR).collect(Collectors.toCollection(ObjectArrayList::new));
        if (c.size() > 1) list.add(list.get(0));
        return original.call(list);
    }

    @Redirect(method = "recalculateCraftingPatterns", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectSet;add(Ljava/lang/Object;)Z"))
    private boolean add(ObjectSet<Object> instance, Object o, @Share("rc$map") LocalRef<ObjectSet<Object>> ref) {
        if (ref.get() != null) {
            var a = ref.get().add(o);
            ref.set(null);
            return a;
        }
        return instance.add(o);
    }
}
