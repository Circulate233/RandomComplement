package com.circulation.random_complement.mixin.ae2fc;

import appeng.helpers.ItemStackHelper;
import com.glodblock.github.network.CPacketLoadPattern;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = CPacketLoadPattern.class, remap = false)
public abstract class MixinCPacketLoadPattern implements IMessage {

    /**
     * @author Circulation
     * @reason 修复方法使得传输的ItemStack的count可以大于127
     */
    @Overwrite
    private void writeItemArray(NBTTagCompound nbt, ItemStack[] itemList, String key) {
        NBTTagCompound dict = new NBTTagCompound();
        dict.setShort("l", (short) (itemList == null ? 0 : itemList.length));
        if (itemList != null) {
            int cnt = 0;
            for (ItemStack item : itemList) {
                if (item != null) {
                    dict.setTag(cnt + "#", ItemStackHelper.stackToNBT(item));
                    cnt++;
                }
            }
            dict.setShort("l", (short) cnt);
        }
        nbt.setTag(key, dict);
    }

    /**
     * @author Circulation
     * @reason 修复方法使得传输的ItemStack的count可以大于127
     */
    @Overwrite
    private ItemStack[] readItemArray(NBTTagCompound nbt, String key) {
        NBTTagCompound dict = nbt.getCompoundTag(key);
        short len = dict.getShort("l");
        if (len == 0) {
            return new ItemStack[0];
        } else {
            ItemStack[] itemList = new ItemStack[len];
            for (int i = 0; i < len; i++) {
                itemList[i] = ItemStackHelper.stackFromNBT(dict.getCompoundTag(i + "#"));
            }
            return itemList;
        }
    }
}
