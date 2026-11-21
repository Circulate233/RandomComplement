package com.circulation.random_complement.mixin.ae2;

import appeng.util.item.ItemList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(value = ItemList.class, remap = false)
public interface AccessorItemList {
    @Accessor("records")
    Reference2ObjectMap<Item, ?> rc$getRecords();

    @Accessor("version")
    AtomicInteger rc$getVersion();
}
