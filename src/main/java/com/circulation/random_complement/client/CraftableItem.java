package com.circulation.random_complement.client;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

public class CraftableItem {
    public Item item;
    public int meta;
    public NBTTagCompound nbt;

    public CraftableItem(ItemStack itemStack){
        this.item = itemStack.getItem();
        this.meta = itemStack.getItemDamage();
        this.nbt = itemStack.getTagCompound();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CraftableItem that = (CraftableItem) o;
        return meta == that.meta &&
                item.getRegistryName().equals(that.item.getRegistryName()) &&
                Objects.equals(nbt, that.nbt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item.getRegistryName(), meta, nbt);
    }
}
