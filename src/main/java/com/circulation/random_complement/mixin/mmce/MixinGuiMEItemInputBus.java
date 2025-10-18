package com.circulation.random_complement.mixin.mmce;

import appeng.container.interfaces.IJEIGhostIngredients;
import appeng.container.slot.IJEITargetSlot;
import appeng.container.slot.SlotFake;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketInventoryAction;
import appeng.helpers.InventoryAction;
import appeng.util.item.AEItemStack;
import github.kasuminova.mmce.client.gui.GuiMEItemBus;
import github.kasuminova.mmce.client.gui.GuiMEItemInputBus;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nonnull;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Mixin(value = GuiMEItemInputBus.class, remap = false)
public abstract class MixinGuiMEItemInputBus extends GuiMEItemBus implements IJEIGhostIngredients {

    @Unique
    private Map<IGhostIngredientHandler.Target<?>, Object> r$mapTargetSlot = new Object2ObjectOpenHashMap<>();

    public MixinGuiMEItemInputBus(Container container) {
        super(container);
    }

    @Intrinsic
    public List<IGhostIngredientHandler.Target<?>> getPhantomTargets(Object ingredient) {
        this.r$mapTargetSlot.clear();
        if (ingredient instanceof ItemStack itemStack) {
            List<IGhostIngredientHandler.Target<?>> targets = new ObjectArrayList<>();
            List<IJEITargetSlot> slots = new ObjectArrayList<>();
            if (!this.inventorySlots.inventorySlots.isEmpty()) {
                for (Slot slot : this.inventorySlots.inventorySlots) {
                    if (slot instanceof SlotFake && !itemStack.isEmpty()) {
                        slots.add((IJEITargetSlot) slot);
                    }
                }
            }

            for (final IJEITargetSlot slot : slots) {
                var targetItem = r$getObjectTarget(itemStack, slot);
                targets.add(targetItem);
                this.r$mapTargetSlot.putIfAbsent(targetItem, slot);
            }

            return targets;
        } else {
            return ObjectLists.emptyList();
        }
    }

    @Unique
    private IGhostIngredientHandler.Target<Object> r$getObjectTarget(ItemStack itemStack, IJEITargetSlot slot) {
        final GuiMEItemBus g = this;
        return new IGhostIngredientHandler.Target<>() {
            @Nonnull
            public Rectangle getArea() {
                if (slot instanceof SlotFake slotFake && slotFake.isSlotEnabled()) {
                    return new Rectangle(g.getGuiLeft() + slotFake.xPos, g.getGuiTop() + slotFake.yPos, 16, 16);
                }
                return new Rectangle();
            }

            public void accept(@Nonnull Object ingredient) {
                try {
                    if (slot instanceof SlotFake && ((SlotFake) slot).isSlotEnabled()) {
                        if (!itemStack.isEmpty()) {
                            PacketInventoryAction p = new PacketInventoryAction(InventoryAction.PLACE_JEI_GHOST_ITEM, slot, AEItemStack.fromItemStack(itemStack));
                            NetworkHandler.instance().sendToServer(p);
                        }
                    }
                } catch (IOException ignored) {

                }

            }
        };
    }

    @Intrinsic
    public Map<IGhostIngredientHandler.Target<?>, Object> getFakeSlotTargetMap() {
        return this.r$mapTargetSlot;
    }
}