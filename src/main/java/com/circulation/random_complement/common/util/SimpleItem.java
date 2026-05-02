package com.circulation.random_complement.common.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ToString
public final class SimpleItem {

    public static final SimpleItem empty = new SimpleItem(ItemStack.EMPTY);
    private static final Map<ResourceLocation, CachedMetaItems> noNbtCache = new ConcurrentHashMap<>();
    private final ResourceLocation item;
    @Getter
    private final int meta;
    private final int hashCode;
    private final NBTTagCompound nbt;

    private SimpleItem(ResourceLocation item, int meta, NBTTagCompound nbt) {
        this.item = item;
        this.meta = meta;
        this.nbt = nbt;
        this.hashCode = computeHash(item, meta, nbt);
    }

    private SimpleItem(ItemStack stack) {
        this(stack.getItem().getRegistryName(), stack.getItemDamage(), copyNBT(stack.getTagCompound()));
    }

    public static SimpleItem getInstance(final String rl, final int meta) {
        return getInstance(new ResourceLocation(rl), meta);
    }

    public static SimpleItem getInstance(final ResourceLocation rl, final int meta) {
        return noNbtCache.computeIfAbsent(rl, CachedMetaItems::new)
                         .computeIfAbsent(meta);
    }

    public static SimpleItem getInstance(final ItemStack stack) {
        if (stack == null || stack.isEmpty()) return empty;
        var nbt = stack.getTagCompound();
        if (nbt == null || nbt.isEmpty()) {
            return getNoNBTInstance(stack);
        }
        return new SimpleItem(stack);
    }

    public static SimpleItem getNoNBTInstance(final ItemStack stack) {
        if (stack.isEmpty()) return empty;
        return getInstance(stack.getItem().getRegistryName(), stack.getItemDamage());
    }

    private static NBTTagCompound copyNBT(NBTTagCompound nbt) {
        if (nbt == null || nbt.isEmpty()) {
            return null;
        }
        return nbt.copy();
    }

    private static int computeHash(ResourceLocation item, int meta, NBTTagCompound nbt) {
        int result = item == null ? 0 : item.hashCode();
        result = 31 * result + meta;
        result = 31 * result + (nbt == null ? 0 : nbt.hashCode());
        return result;
    }

    public Item getItem() {
        return Item.REGISTRY.getObject(item);
    }

    public ResourceLocation getRegistryName() {
        return item;
    }

    public String getItemID() {
        return item.toString();
    }

    public ItemStack getItemStack(int amount) {
        var i = new ItemStack(getItem(), amount, meta);
        if (nbt != null && !nbt.isEmpty()) {
            i.setTagCompound(nbt.copy());
        }
        return i;
    }

    public boolean isEmpty() {
        return this == empty;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SimpleItem that = (SimpleItem) o;
        return meta == that.meta && Objects.equals(item, that.item) && Objects.equals(nbt, that.nbt);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private static class CachedMetaItems extends Int2ObjectOpenHashMap<SimpleItem> {
        private final ResourceLocation item;

        private CachedMetaItems(ResourceLocation item) {
            this.item = item;
        }

        public SimpleItem computeIfAbsent(int key) {
            SimpleItem v;
            if ((v = get(key)) == null) {
                synchronized (this) {
                    if ((v = get(key)) == null) {
                        v = new SimpleItem(item, key, null);
                        put(key, v);
                    }
                }
            }

            return v;
        }

    }
}
