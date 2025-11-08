package com.circulation.random_complement.mixin.ae2fc.jei;

import appeng.api.storage.data.IAEItemStack;
import appeng.container.slot.SlotFake;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketInventoryAction;
import appeng.helpers.InventoryAction;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.common.util.MEHandler;
import com.glodblock.github.common.item.fake.FakeFluids;
import com.glodblock.github.integration.jei.FluidPacketTarget;
import com.glodblock.github.integration.mek.FakeGases;
import mekanism.api.gas.GasStack;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;
import java.io.IOException;

@Mixin(value = FluidPacketTarget.class, remap = false)
public abstract class MixinFluidPacketTarget {

    @Shadow
    @Final
    private Slot slot;

    /**
     * @author circulation
     * @reason 在鼠标左键时写入流体，右键写入流体容器
     */
    @Overwrite
    public void accept(@Nonnull Object ingredient) {
        final IAEItemStack packet;
        if (!(ingredient instanceof ItemStack stack) || Mouse.getEventButton() == 0) {
            FluidStack fluid = MEHandler.covertFluid(ingredient);
            Object gas = MEHandler.covertGas(ingredient);
            if (fluid != null || gas != null) {
                if (fluid != null) {
                    packet = FakeFluids.packFluid2AEPacket(fluid);
                } else {
                    packet = FakeGases.packGas2AEPacket((GasStack) gas);
                }
            } else return;
        } else {
            packet = AEItemStack.fromItemStack(stack);
        }

        try {
            PacketInventoryAction p = new PacketInventoryAction(InventoryAction.PLACE_JEI_GHOST_ITEM, (SlotFake) this.slot, packet);
            NetworkHandler.instance().sendToServer(p);
        } catch (IOException ignored) {

        }
    }

}