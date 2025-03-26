package com.circulation.random_complement.client;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class CraftableItem {
    public ResourceLocation item;
    public int meta;
    public NBTTagCompound nbt;

    public CraftableItem(ItemStack itemStack){
        this.item = itemStack.getItem().getRegistryName();
        this.meta = itemStack.getItemDamage();
        this.nbt = itemStack.getTagCompound();
    }

    public CraftableItem(ResourceLocation item,int meta,NBTTagCompound nbt){
        this.item = item;
        this.meta = meta;
        this.nbt = nbt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CraftableItem that = (CraftableItem) o;
        return meta == that.meta &&
                item.equals(that.item) &&
                (nbt != null && that.nbt != null) ? nbt.equals(that.nbt) : (nbt == null && that.nbt == null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, meta, nbt);
    }
}
