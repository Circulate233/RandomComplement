package com.circulation.random_complement.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class CraftableItem {
    public String str;

    private CraftableItem(ItemStack itemStack){
        var key = new StringBuilder(itemStack.getItem().getRegistryName().toString()).append(itemStack.getItemDamage());
        if (itemStack.hasTagCompound()) {
            key.append(itemStack.getTagCompound().hashCode());
        }
        this.str = key.toString();
    }

    private CraftableItem(String str){
        this.str = str;
    }

    private static final LoadingCache<String, CraftableItem> CRAFTABLE_ITEM_POOL =
            CacheBuilder.newBuilder()
                    .maximumSize(10000)
                    .weakValues()
                    .build(new CacheLoader<>() {
                        @Override
                        public CraftableItem load(@NotNull String str) {
                            return new CraftableItem(str);
                        }
                    });

    public static CraftableItem getInstance(@NotNull ItemStack itemStack) {
        try {
            var key = new StringBuilder(itemStack.getItem().getRegistryName().toString()).append(itemStack.getItemDamage());
            if (itemStack.hasTagCompound()) {
                key.append(itemStack.getTagCompound().hashCode());
            }
            return CRAFTABLE_ITEM_POOL.get(key.toString());
        } catch (ExecutionException e) {
            return new CraftableItem("null");
        }
    }

}
