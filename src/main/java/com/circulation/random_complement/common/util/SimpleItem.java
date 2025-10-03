package com.circulation.random_complement.common.util;

import appeng.api.storage.data.IAEItemStack;
import com.github.bsideup.jabel.Desugar;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

@Desugar
public record SimpleItem(@NotNull String str) {
    public static final SimpleItem empty = new SimpleItem("e");

    private static final LoadingCache<String, SimpleItem> CRAFTABLE_ITEM_POOL =
            CacheBuilder.newBuilder()
                    .maximumSize(10000)
                    .weakValues()
                    .build(new CacheLoader<>() {
                        @Override
                        public SimpleItem load(@NotNull String str) {
                            return new SimpleItem(str);
                        }
                    });

    public static SimpleItem getInstance(@NotNull ItemStack itemStack) {
        try {
            if (itemStack.isEmpty()) return empty;
            var key = new StringBuilder(itemStack.getItem().getRegistryName().toString()).append(itemStack.getItemDamage());
            if (itemStack.hasTagCompound()) {
                key.append(itemStack.getTagCompound().hashCode());
            }
            return CRAFTABLE_ITEM_POOL.get(key.toString());
        } catch (ExecutionException e) {
            return empty;
        }
    }

    public static SimpleItem getInstance(@NotNull IAEItemStack itemStack) {
        return getInstance(itemStack.getDefinition());
    }

    public boolean isEmpty() {
        return empty.equals(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)return true;
        if (obj instanceof SimpleItem si) {
            return this.str.equals(si.str);
        }
        return false;
    }

    @Override
    public @NotNull String toString() {
        return this.str;
    }

}