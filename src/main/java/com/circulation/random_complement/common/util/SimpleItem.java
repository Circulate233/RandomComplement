package com.circulation.random_complement.common.util;

import appeng.api.storage.data.IAEItemStack;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public final class SimpleItem {
    @NotNull
    public String str;
    private static final SimpleItem empty = new SimpleItem("e");

    private SimpleItem(ItemStack itemStack){
        var key = new StringBuilder(itemStack.getItem().getRegistryName().toString()).append(itemStack.getItemDamage());
        if (itemStack.hasTagCompound()) {
            key.append(itemStack.getTagCompound().hashCode());
        }
        this.str = key.toString();
    }

    private SimpleItem(@NotNull String str){
        this.str = str;
    }

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
            if (itemStack.isEmpty())return empty;
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

    public boolean isEmpty(){
        return this == empty;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleItem si) {
            return this.toString().equals(si.toString());
        }
        return false;
    }

    @Override
    public String toString(){
        return this.str;
    }

    @Override
    public int hashCode(){
        return this.str.hashCode();
    }

}
