package com.circulation.random_complement.mixin.ae2;

import appeng.util.item.ItemList;
import com.circulation.random_complement.common.interfaces.RCItemList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ItemList.class, remap = false)
public class MixinItemList implements RCItemList {

    @Mutable
    @Shadow
    @Final
    private Reference2ObjectMap<Object, Object> records;

    @Override
    public void rc$reserveCapacity(int expected) {
        if (expected <= 0) {
            return;
        }
        int reserve = Math.min(expected, RCItemList.RC$MAX_RESERVE);
        if (records.size() >= reserve) {
            return;
        }
        var replacement = new Reference2ObjectOpenHashMap<>(reserve);
        replacement.putAll(records);
        this.records = replacement;
    }
}