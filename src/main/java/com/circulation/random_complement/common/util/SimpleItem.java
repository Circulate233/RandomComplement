package com.circulation.random_complement.common.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class SimpleItem {
    public String str;
    private static final SimpleItem empty = new SimpleItem("e");

    private SimpleItem(ItemStack itemStack){
        var key = new StringBuilder(itemStack.getItem().getRegistryName().toString()).append(itemStack.getItemDamage());
        if (itemStack.hasTagCompound()) {
            key.append(itemStack.getTagCompound().hashCode());
        }
        this.str = key.toString();
    }

    private SimpleItem(String str){
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

    public boolean isEmpty(){
        return this == empty;
    }

}
