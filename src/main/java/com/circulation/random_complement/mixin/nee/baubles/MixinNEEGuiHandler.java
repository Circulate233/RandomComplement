package com.circulation.random_complement.mixin.nee.baubles;

import appeng.api.util.AEPartLocation;
import appeng.helpers.WirelessTerminalGuiObject;
import baubles.api.BaublesApi;
import com.github.vfyjxf.nee.client.gui.ConfirmWrapperGui;
import com.github.vfyjxf.nee.container.ContainerCraftingConfirmWrapper;
import com.github.vfyjxf.nee.network.NEEGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.IGuiHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NEEGuiHandler.class, remap = false)
public abstract class MixinNEEGuiHandler implements IGuiHandler {

    @Inject(method = "getServerGuiElement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/InventoryPlayer;getStackInSlot(I)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true, remap = true)
    public void getServerGuiElementMixin(int ordinal, EntityPlayer player, World world, int x, int y, int z, CallbackInfoReturnable<Object> cir) {
        if (y == 1 && Loader.isModLoaded("baubles")) {
            ItemStack it = BaublesApi.getBaublesHandler(player).getStackInSlot(x);
            WirelessTerminalGuiObject guiObject = this.getGuiObject(it, player, world, x, y, z);
            if (guiObject != null) {
                cir.setReturnValue(updateGui(new ContainerCraftingConfirmWrapper(player.inventory, guiObject), world, x, y, z, AEPartLocation.INTERNAL, null));
            }
        }
    }

    @Inject(method = "getClientGuiElement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/InventoryPlayer;getStackInSlot(I)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true, remap = true)
    public void getClientGuiElementMixin(int ordinal, EntityPlayer player, World world, int x, int y, int z, CallbackInfoReturnable<Object> cir) {
        if (y == 1 && Loader.isModLoaded("baubles")) {
            ItemStack it = BaublesApi.getBaublesHandler(player).getStackInSlot(x);
            WirelessTerminalGuiObject guiObject = this.getGuiObject(it, player, world, x, y, z);
            if (guiObject != null) {
                cir.setReturnValue(new ConfirmWrapperGui(player.inventory, guiObject));
            }
        }
    }

    @Shadow
    private Object updateGui(Object newContainer, final World w, final int x, final int y, final int z, final AEPartLocation side, final Object myItem) {
        return null;
    }

    @Shadow
    private WirelessTerminalGuiObject getGuiObject(ItemStack it, EntityPlayer player, World w, int x, int y, int z) {
        return null;
    }
}