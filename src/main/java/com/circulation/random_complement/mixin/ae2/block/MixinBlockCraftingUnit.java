package com.circulation.random_complement.mixin.ae2.block;

import appeng.block.AEBaseTileBlock;
import appeng.block.crafting.BlockCraftingUnit;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.tile.crafting.TileCraftingTile;
import appeng.util.Platform;
import com.circulation.random_complement.common.handler.CraftingUnitHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockCraftingUnit.class,remap = false)
public class MixinBlockCraftingUnit extends AEBaseTileBlock {

    public MixinBlockCraftingUnit(Material mat) {
        super(mat);
    }

    @Inject(method = "onBlockActivated", at = @At("HEAD"), remap = true, cancellable = true)
    public void onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (Platform.isServer()) {
            TileCraftingTile tg = this.getTileEntity(w, pos);
            boolean isBusy = tg.getCluster() instanceof CraftingCPUCluster && ((CraftingCPUCluster) tg.getCluster()).isBusy();
            ItemStack item = p.getHeldItem(hand);
            if (p.isSneaking()) {
                if (item.isEmpty()) {
                    if (isBusy) {
                        p.sendStatusMessage(new TextComponentTranslation("error.rc.cpu"), true);
                        cir.setReturnValue(true);
                    } else if (CraftingUnitHandler.isReplaceable(null, this)) {
                        w.setBlockState(pos, CraftingUnitHandler.getCraftingUnitBase(this).getBlockState().getBaseState());
                        p.inventory.placeItemBackInInventory(w, CraftingUnitHandler.getMatchItem(this));
                        cir.setReturnValue(true);
                    }
                }
            } else if (CraftingUnitHandler.isReplaceable(item, this)) {
                if (isBusy) {
                    p.sendStatusMessage(new TextComponentTranslation("error.rc.cpu"), true);
                    cir.setReturnValue(true);
                } else {
                    w.setBlockState(pos, CraftingUnitHandler.getMatchBlock(item).getBlockState().getBaseState());
                    item.shrink(1);
                    var mItem = CraftingUnitHandler.getMatchItem(this);
                    if (!mItem.isEmpty()) {
                        p.inventory.placeItemBackInInventory(w, mItem);
                    }
                    cir.setReturnValue(true);
                }
            }
        }
    }
}