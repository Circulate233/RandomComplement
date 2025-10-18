package com.circulation.random_complement.mixin.ae2;

import appeng.api.util.AEPartLocation;
import appeng.core.sync.GuiBridge;
import appeng.items.contents.NetworkToolViewer;
import appeng.tile.networking.TileController;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GuiBridge.class, remap = false)
public abstract class MixinGuiBridge {

    @Shadow
    protected abstract Object getGuiObject(ItemStack it, EntityPlayer player, World w, int x, int y, int z);

    @Inject(method = "hasPermissions", at = @At("HEAD"), cancellable = true)
    public void hasPermissions(TileEntity te, int x, int y, int z, AEPartLocation side, EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == GuiBridge.GUI_NETWORK_STATUS) {
            if (te instanceof TileController) {
                cir.setReturnValue(true);
            }
        }
    }

    @WrapOperation(method = "getServerGuiElement", at = @At(value = "INVOKE", target = "Lappeng/core/sync/GuiBridge;getGuiObject(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;III)Ljava/lang/Object;"))
    public Object getServerGui(GuiBridge instance, ItemStack it, EntityPlayer p, World w, int x, int y, int z, Operation<Object> original, @Local(name = "ID") GuiBridge ID) {
        if (ID == GuiBridge.GUI_NETWORK_STATUS) {
            if (z == Integer.MIN_VALUE) return original.call(instance, it, p, w, x, y, z);
            if (w.getTileEntity(new BlockPos(x, y, z)) instanceof TileController te) {
                return new NetworkToolViewer(it, te);
            }
        }
        return original.call(instance, it, p, w, x, y, z);
    }

    @WrapOperation(method = "getClientGuiElement", at = @At(value = "INVOKE", target = "Lappeng/core/sync/GuiBridge;getGuiObject(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;III)Ljava/lang/Object;"))
    public Object getClientGui(GuiBridge instance, ItemStack it, EntityPlayer p, World w, int x, int y, int z, Operation<Object> original, @Local(name = "ID") GuiBridge ID) {
        if (ID == GuiBridge.GUI_NETWORK_STATUS) {
            if (z == Integer.MIN_VALUE) return original.call(instance, it, p, w, x, y, z);
            if (w.getTileEntity(new BlockPos(x, y, z)) instanceof TileController te) {
                return new NetworkToolViewer(it, te);
            }
        }
        return original.call(instance, it, p, w, x, y, z);
    }
}