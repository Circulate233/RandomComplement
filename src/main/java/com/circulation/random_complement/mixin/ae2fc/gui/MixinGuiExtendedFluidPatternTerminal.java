package com.circulation.random_complement.mixin.ae2fc.gui;

import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.implementations.GuiExpandedProcessingPatternTerm;
import appeng.container.slot.SlotFake;
import com.circulation.random_complement.client.CraftableItem;
import com.circulation.random_complement.common.handler.MEHandler;
import com.glodblock.github.client.GuiExtendedFluidPatternTerminal;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mixin(value = GuiExtendedFluidPatternTerminal.class)
public class MixinGuiExtendedFluidPatternTerminal extends GuiExpandedProcessingPatternTerm {

    @Unique
    private Set<CraftableItem> randomComplement$craftableCache = new HashSet<>();

    public MixinGuiExtendedFluidPatternTerminal(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Unique
    private Set<IAEItemStack> randomComplement$getStorage() {
        IItemList<IAEItemStack> all = this.repo.getList();
        return all == null ? Collections.emptySet() : StreamSupport.stream(all.spliterator(), false).collect(Collectors.toSet());
    }

    @Unique
    private Set<CraftableItem> randomComplement$getCraftables() {
        if (randomComplement$craftableCache.isEmpty()) {
            randomComplement$craftableCache = this.randomComplement$getStorage()
                    .stream()
                    .filter(IAEStack::isCraftable)
                    .map(itemStack -> new CraftableItem(itemStack.getDefinition()))
                    .collect(Collectors.toSet());
        }

        return randomComplement$craftableCache;
    }

    @Inject(method = "drawSlot", at = @At(value = "HEAD"))
    private void drawSlotFake(Slot slot, CallbackInfo ci) {
        if (slot instanceof SlotFake slotFake) {
            if (!slotFake.getDisplayStack().isEmpty()) {
                if (randomComplement$getCraftables().contains(new CraftableItem(slotFake.getDisplayStack()))) {
                    MEHandler.drawPlus(slotFake);
                }
            }
        }
    }
}
