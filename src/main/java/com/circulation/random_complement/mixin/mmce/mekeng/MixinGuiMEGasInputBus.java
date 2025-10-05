package com.circulation.random_complement.mixin.mmce.mekeng;

import appeng.api.implementations.IUpgradeableHost;
import appeng.client.gui.implementations.GuiUpgradeable;
import appeng.client.gui.widgets.GuiCustomSlot;
import com.mekeng.github.MekEng;
import com.mekeng.github.client.slots.SlotGas;
import com.mekeng.github.common.me.data.impl.AEGasStack;
import com.mekeng.github.network.packet.CGasSlotSync;
import com.mekeng.github.util.Utils;
import github.kasuminova.mmce.client.gui.GuiMEGasInputBus;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import mekanism.api.gas.GasStack;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nonnull;
import java.awt.Rectangle;
import java.util.List;

@Mixin(value = GuiMEGasInputBus.class,remap = false)
public class MixinGuiMEGasInputBus extends GuiUpgradeable {

    public MixinGuiMEGasInputBus(InventoryPlayer inventoryPlayer, IUpgradeableHost te) {
        super(inventoryPlayer, te);
    }

    @Intrinsic
    public List<IGhostIngredientHandler.Target<?>> getPhantomTargets(Object ingredient) {
        this.mapTargetSlot.clear();
        List<IGhostIngredientHandler.Target<?>> targets = new ObjectArrayList<>();
        final GasStack gasStack;

        if (ingredient instanceof ItemStack stack) {
            gasStack = Utils.getGasFromItem(stack);
        } else if (ingredient instanceof GasStack g) {
            gasStack = g;
        } else {
            gasStack = null;
        }

        if (gasStack == null) return ObjectLists.emptyList();

        List<SlotGas> slots = new ObjectArrayList<>();
        if (!this.getGuiSlots().isEmpty()) {
            for (GuiCustomSlot slot : this.getGuiSlots()) {
                if (slot instanceof SlotGas s) {
                    slots.add(s);
                }
            }
        }

        for (final SlotGas slot : slots) {
            var targetItem = r$getTarget(slot, gasStack);
            targets.add(targetItem);
            this.mapTargetSlot.putIfAbsent(targetItem, slot);
        }

        return targets;
    }

    @Unique
    private IGhostIngredientHandler.Target<?> r$getTarget(SlotGas slot, GasStack gasStack) {
        final GuiUpgradeable gui = this;
        return new IGhostIngredientHandler.Target<>() {

            @Nonnull
            public Rectangle getArea() {
                return slot.isSlotEnabled() ? new Rectangle(gui.getGuiLeft() + slot.xPos(), gui.getGuiTop() + slot.yPos(), 16, 16) : new Rectangle();
            }

            public void accept(@Nonnull Object ingredient) {
                MekEng.proxy.netHandler.sendToServer(new CGasSlotSync(Int2ObjectMaps.singleton(slot.getId(), AEGasStack.of(gasStack))));
            }
        };
    }
}