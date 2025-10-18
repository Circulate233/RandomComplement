package com.circulation.random_complement.mixin.ae2.block;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.security.ISecurityGrid;
import appeng.block.AEBaseTileBlock;
import appeng.block.crafting.BlockCraftingUnit;
import appeng.core.worlddata.WorldData;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.crafting.TileCraftingTile;
import appeng.util.Platform;
import com.circulation.random_complement.RCConfig;
import com.circulation.random_complement.common.handler.CraftingUnitHandler;
import com.mojang.authlib.GameProfile;
import lombok.val;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockCraftingUnit.class, remap = false)
public class MixinBlockCraftingUnit extends AEBaseTileBlock {

    public MixinBlockCraftingUnit(Material mat) {
        super(mat);
    }

    @Inject(method = "onBlockActivated", at = @At("HEAD"), remap = true, cancellable = true)
    public void onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (Platform.isServer()) {
            if (p instanceof FakePlayer) return;
            TileCraftingTile tg = this.getTileEntity(w, pos);
            boolean isBusy = tg.getCluster() instanceof CraftingCPUCluster && ((CraftingCPUCluster) tg.getCluster()).isBusy();
            ItemStack item = p.getHeldItem(hand);
            if (p.isSneaking()) {
                if (item.isEmpty() && RCConfig.AE2.SecurityCache) {
                    if (isBusy) {
                        p.sendStatusMessage(new TextComponentTranslation("error.rc.cpu"), true);
                        cir.setReturnValue(true);
                    } else if (CraftingUnitHandler.isReplaceable(null, this)) {
                        val block = CraftingUnitHandler.getCraftingUnitBase(this);
                        if (block == (Object) this) return;
                        w.setBlockState(pos, block.getBlockState().getBaseState(), 1);
                        p.inventory.placeItemBackInInventory(w, CraftingUnitHandler.getMatchItem(this));
                        cir.setReturnValue(true);
                    }
                }
            } else if (!item.isEmpty() && CraftingUnitHandler.isReplaceable(item, this)) {
                if (isBusy) {
                    p.sendStatusMessage(new TextComponentTranslation("error.rc.cpu"), true);
                    cir.setReturnValue(true);
                } else {
                    val block = CraftingUnitHandler.getMatchBlock(item);
                    if (block == (Object) this) return;
                    var tile = this.getTileEntity(w, pos);
                    var proxy = ((IGridProxyable) tile).getProxy();
                    var node = proxy.getNode();
                    Object cache;
                    if (RCConfig.AE2.SecurityCache || ((cache = node.getGrid().getCache(ISecurityGrid.class)) instanceof ISecurityGrid isg && isg.hasPermission(p, SecurityPermissions.BUILD))) {
                        w.setBlockState(pos, block.getBlockState().getBaseState(), 1);
                        tile = this.getTileEntity(w, pos);
                        proxy = ((IGridProxyable) tile).getProxy();
                        node = proxy.getNode();
                        if (node == null) {
                            proxy.onReady();
                            node = proxy.getNode();
                            final GameProfile profile = p.getGameProfile();
                            node.setPlayerID(WorldData.instance().playerData().getPlayerID(profile));
                        }
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
}