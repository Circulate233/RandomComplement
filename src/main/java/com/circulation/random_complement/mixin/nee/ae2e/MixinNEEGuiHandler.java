package com.circulation.random_complement.mixin.nee.ae2e;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.storage.ITerminalHost;
import appeng.api.util.AEPartLocation;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.helpers.WirelessTerminalGuiObject;
import com._0xc4de.ae2exttable.part.PartSharedCraftingTerminal;
import com.github.vfyjxf.nee.client.gui.ConfirmWrapperGui;
import com.github.vfyjxf.nee.network.NEEGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.vfyjxf.nee.network.NEEGuiHandler.CONFIRM_WRAPPER_ID;

@Mixin(value = NEEGuiHandler.class, remap = false)
public abstract class MixinNEEGuiHandler implements IGuiHandler {

    @Inject(method = "getServerGuiElement", at = @At(value = "INVOKE", target = "Lappeng/api/parts/IPartHost;getPart(Lappeng/api/util/AEPartLocation;)Lappeng/api/parts/IPart;", shift = At.Shift.AFTER), cancellable = true)
    public void getServerGuiElementMixin1(int ordinal, EntityPlayer player, World world, int x, int y, int z, CallbackInfoReturnable<Object> cir) {
        final int guiId = ordinal >> 8;
        final AEPartLocation side = AEPartLocation.fromOrdinal(ordinal & 7);
        IPartHost partHost = (IPartHost) world.getTileEntity(new BlockPos(x, y, z));
        IPart part = partHost.getPart(side);
        if (guiId == CONFIRM_WRAPPER_ID) {
            if (part instanceof PartSharedCraftingTerminal) {
                cir.setReturnValue(updateGui(new ContainerCraftConfirm(player.inventory, (ITerminalHost) part), world, x, y, z, side, part));
            }
        }
    }

    @Inject(method = "getClientGuiElement", at = @At(value = "INVOKE", target = "Lappeng/api/parts/IPartHost;getPart(Lappeng/api/util/AEPartLocation;)Lappeng/api/parts/IPart;", shift = At.Shift.AFTER), cancellable = true)
    public void getClientGuiElementMixin1(int ordinal, EntityPlayer player, World world, int x, int y, int z, CallbackInfoReturnable<Object> cir) {
        final int guiId = ordinal >> 8;
        final AEPartLocation side = AEPartLocation.fromOrdinal(ordinal & 7);
        IPartHost partHost = (IPartHost) world.getTileEntity(new BlockPos(x, y, z));
        IPart part = partHost.getPart(side);
        if (guiId == CONFIRM_WRAPPER_ID) {
            if (part instanceof PartSharedCraftingTerminal) {
                cir.setReturnValue(updateGui(new ConfirmWrapperGui(player.inventory, (ITerminalHost) part), world, x, y, z, side, part));
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