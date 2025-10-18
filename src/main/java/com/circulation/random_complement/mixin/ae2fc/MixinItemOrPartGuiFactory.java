package com.circulation.random_complement.mixin.ae2fc;

import baubles.api.BaublesApi;
import com.glodblock.github.inventory.GuiType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(value = GuiType.ItemOrPartGuiFactory.class, remap = false)
public abstract class MixinItemOrPartGuiFactory<T> extends GuiType.TileGuiFactory<T> {

    public MixinItemOrPartGuiFactory(Class<T> invClass) {
        super(invClass);
    }

    @Unique
    @Optional.Method(modid = "baubles")
    private static ItemStack randomComplement$getStackInBaubleSlot(EntityPlayer player, int slot) {
        return slot >= 0 && slot < BaublesApi.getBaublesHandler(player).getSlots() ? BaublesApi.getBaublesHandler(player).getStackInSlot(slot) : ItemStack.EMPTY;
    }

    /**
     * @author Circulation
     * @reason 修复bug
     */
    @Nullable
    @Overwrite
    protected T getInventory(TileEntity tile, EntityPlayer player, EnumFacing face, BlockPos pos) {
        if (pos.getZ() == Integer.MIN_VALUE) {
            ItemStack terminal = ItemStack.EMPTY;
            if (pos.getY() == 0) {
                terminal = player.inventory.getStackInSlot(pos.getX());
            } else if (pos.getY() == 1 && Loader.isModLoaded("baubles")) {
                terminal = randomComplement$getStackInBaubleSlot(player, pos.getX());
            }

            if (terminal.isEmpty()) {
                return null;
            }

            Object holder = GuiType.getItemGuiObject(terminal, player, player.world, pos.getX(), pos.getY(), pos.getZ());
            if (this.invClass.isInstance(holder)) {
                return this.invClass.cast(holder);
            }
        }

        return super.getInventory(tile, player, face, pos);
    }
}
