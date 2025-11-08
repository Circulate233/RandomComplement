package com.circulation.random_complement.mixin.ae2fc.gui;

import appeng.container.slot.SlotFake;
import com.circulation.random_complement.client.ClientProxy;
import com.circulation.random_complement.common.util.Functions;
import com.circulation.random_complement.common.util.MEHandler;
import com.circulation.random_complement.mixin.ae2.gui.MixinGuiMEMonitorable;
import com.glodblock.github.client.GuiExtendedFluidPatternTerminal;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiExtendedFluidPatternTerminal.class)
public abstract class MixinGuiExtendedFluidPatternTerminal extends MixinGuiMEMonitorable {

    public MixinGuiExtendedFluidPatternTerminal(Container container) {
        super(container);
    }

    @Inject(method = "drawSlot", at = @At(value = "HEAD"))
    private void drawSlotFake(Slot slot, CallbackInfo ci) {
        if (slot.xPos < 0 || slot.yPos < 0) return;
        if (slot instanceof SlotFake slotFake) {
            if (!slotFake.shouldDisplay()) return;
            var item = slotFake.getDisplayStack();
            if (!item.isEmpty()) {
                if (r$getCraftablesCache().contains(MEHandler.packAEItem(item))) {
                    r$getPlusSlot().add(slotFake);
                }
            }
        }
    }

    @Inject(method = "drawFG", at = @At("TAIL"), remap = false)
    private void onDrawFG(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        var slot = this.hoveredSlot;
        if (slot instanceof SlotFake) {
            var item = ClientProxy.getMouseItem();
            if (item == null) return;

            var f = MEHandler.covertFluid(item);
            if (f != null) {
                this.drawHoveringText(
                    Functions.asList(
                        I18n.format("text.re.fluid_pattern.tooltip", GameSettings.getKeyDisplayString(-100), f.getLocalizedName()),
                        I18n.format("text.re.fluid_pattern.tooltip", GameSettings.getKeyDisplayString(-99), item.getDisplayName())
                    ),
                    mouseX - offsetX,
                    mouseY - offsetY
                );
                return;
            }
            var g = MEHandler.covertGas(item);
            if (g != null) {
                this.drawHoveringText(
                    Functions.asList(
                        I18n.format("text.re.fluid_pattern.tooltip", GameSettings.getKeyDisplayString(-100), MEHandler.getGasName(g)),
                        I18n.format("text.re.fluid_pattern.tooltip", GameSettings.getKeyDisplayString(-99), item.getDisplayName())
                    ),
                    mouseX - offsetX,
                    mouseY - offsetY
                );
            }
        }
    }
}