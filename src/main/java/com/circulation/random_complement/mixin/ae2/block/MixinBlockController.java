package com.circulation.random_complement.mixin.ae2.block;

import appeng.api.util.AEPartLocation;
import appeng.block.AEBaseTileBlock;
import appeng.block.networking.BlockController;
import appeng.core.sync.GuiBridge;
import appeng.util.Platform;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BlockController.class,remap = false)
public class MixinBlockController extends AEBaseTileBlock {

    public MixinBlockController(Material mat) {
        super(mat);
    }

    @Intrinsic
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (Platform.isServer() && !player.isSneaking()) {
            if (!(Block.getBlockFromItem(player.getHeldItem(hand).getItem()) instanceof BlockController)){
                Platform.openGUI(player, this.getTileEntity(world, pos), AEPartLocation.fromFacing(facing), GuiBridge.GUI_NETWORK_STATUS);
                return true;
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }
}
