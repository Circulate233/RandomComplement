package com.circulation.random_complement.mixin.ae2.new_patten_gui;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.widgets.GuiScrollbar;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.OptionalSlotFake;
import appeng.container.slot.SlotFakeCraftingMatrix;
import appeng.parts.reporting.AbstractPartEncoder;
import com.circulation.random_complement.client.RCSlotFakeCraftingMatrix;
import com.circulation.random_complement.client.RCSlotPatternOutputs;
import com.circulation.random_complement.common.interfaces.RCPatternEncoder;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerPatternEncoder.class)
public abstract class MixinContainerPatternEncoder extends ContainerMEMonitorable implements RCPatternEncoder, IOptionalSlotHost {

    @Shadow(remap = false)
    protected SlotFakeCraftingMatrix[] craftingSlots;

    @Shadow(remap = false)
    protected OptionalSlotFake[] outputSlots;

    @Shadow(remap = false)
    protected IItemHandler crafting;

    @Unique
    protected RCSlotFakeCraftingMatrix[][] r$craftingSlotGroup;

    @Unique
    protected RCSlotPatternOutputs[][] r$outputSlotGroup;

    @Unique
    protected GuiScrollbar r$guiScrollbar;

    public MixinContainerPatternEncoder(InventoryPlayer ip, ITerminalHost monitorable) {
        super(ip, monitorable);
    }

    @Intrinsic
    public void setScrollbar(GuiScrollbar scrollbar) {
        if (!isRCPatternEncoder()) return;
        r$guiScrollbar = scrollbar;
        setRCSlot();
    }

    @Intrinsic
    public void setRCSlot(int i) {
        if (!isRCPatternEncoder()) return;
        if (this.isCraftingMode()) i = 0;
        for (int j = 0; j < 9; ++j) {
            if (j == i) {
                for (int y = 0; y < 3; ++y) {
                    for (int x = 0; x < 3; ++x) {
                        var input = r$craftingSlotGroup[j][x + y * 3];
                        input.xPos = input.getX();
                        input.visible = true;
                    }
                    var output = r$outputSlotGroup[j][y];
                    output.xPos = this.isCraftingMode() ? -9000 : output.getX();
                    output.visible = true;
                }
            } else {
                for (var slot : r$craftingSlotGroup[j]) {
                    slot.xPos = -9000;
                    slot.visible = false;
                }
                for (var slot : r$outputSlotGroup[j]) {
                    slot.xPos = -9000;
                    slot.visible = false;
                }
            }
        }
    }

    @Intrinsic
    public void setRCSlot() {
        if (!isRCPatternEncoder()) return;
        if (r$guiScrollbar == null) return;
        setRCSlot(r$guiScrollbar.getCurrentScroll());
    }

    @Inject(method = "updateOrderOfOutputSlots", at = @At("HEAD"), remap = false)
    protected void updateOrderOfOutputSlots(CallbackInfo ci) {
        if (!isRCPatternEncoder()) return;
        if (this.isCraftingMode()) {
            setRCSlot(0);
        } else {
            setRCSlot();
        }
    }

    @Redirect(method = "getInputs", at = @At(value = "FIELD", args = "array=length", target = "Lappeng/container/implementations/ContainerPatternEncoder;craftingSlots:[Lappeng/container/slot/SlotFakeCraftingMatrix;"), remap = false)
    protected int getInputs(SlotFakeCraftingMatrix[] array) {
        var o = array.length;
        if (!isRCPatternEncoder()) return o;
        if (this.isCraftingMode()) {
            return Math.min(9, o);
        }
        return o;
    }

    @Redirect(method = "putStackInSlot", at = @At(value = "INVOKE", target = "Lappeng/container/implementations/ContainerPatternEncoder;getAndUpdateOutput()Lnet/minecraft/item/ItemStack;", remap = false))
    public ItemStack putStackInSlot(ContainerPatternEncoder instance, @Local(name = "slotID") int slotID) {
        if (isCraftingMode() && (!isRCPatternEncoder() || isCraftingSlot(slotID))) {
            return getAndUpdateOutput();
        }
        return null;
    }

    @Shadow(remap = false)
    public abstract AbstractPartEncoder getPart();

    @Shadow(remap = false)
    public abstract boolean isCraftingMode();

    @Shadow(remap = false)
    protected abstract ItemStack getAndUpdateOutput();

    @Intrinsic
    public final boolean isRCPatternEncoder() {
        return r$craftingSlotGroup != null;
    }

    @Intrinsic
    public final boolean isCraftingSlot(int slotID) {
        for (var slot : r$craftingSlotGroup[0]) {
            if (slot.slotNumber == slotID) return true;
        }
        return false;
    }
}