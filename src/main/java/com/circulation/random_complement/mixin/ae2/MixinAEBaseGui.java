package com.circulation.random_complement.mixin.ae2;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.client.me.SlotME;
import appeng.container.slot.SlotFake;
import com.circulation.random_complement.client.CraftableItem;
import com.circulation.random_complement.client.handler.MEHandler;
import net.minecraft.client.Minecraft;
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

@Mixin(value = AEBaseGui.class)
public abstract class MixinAEBaseGui{

    @Unique
    private Set<CraftableItem> randomComplement$craftableCache = new HashSet<>();

    @Unique
    private Set<IAEItemStack> randomComplement$getStorage(GuiMEMonitorable gui) {
        IItemList<IAEItemStack> all = ((AccessorGuiMEMonitorable)gui).getRepo().getList();
        return all == null ? Collections.emptySet() : StreamSupport.stream(all.spliterator(), false).collect(Collectors.toSet());
    }

    @Unique
    private Set<CraftableItem> randomComplement$getCraftables(GuiMEMonitorable gui) {
        if (randomComplement$craftableCache.isEmpty()) {
            randomComplement$craftableCache = this.randomComplement$getStorage(gui)
                    .stream()
                    .filter(IAEStack::isCraftable)
                    .map(itemStack -> new CraftableItem(itemStack.getDefinition()))
                    .collect(Collectors.toSet());
        }

        return randomComplement$craftableCache;
    }

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lappeng/client/render/StackSizeRenderer;renderStackSize(Lnet/minecraft/client/gui/FontRenderer;Lappeng/api/storage/data/IAEItemStack;II)V", shift = At.Shift.AFTER ,ordinal = 0,remap = false))
    private void drawSlotME(Slot slot, CallbackInfo ci) {
        if (slot instanceof SlotME slotME) {
            var aeStack = slotME.getAEStack();
            if (aeStack != null && aeStack.isCraftable()) {
                MEHandler.drawPlus(slot.xPos, slot.yPos);
            }
        }
    }

    @Inject(method = "drawSlot", at = @At(value = "HEAD"))
    private void drawSlotFake(Slot slot, CallbackInfo ci) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiPatternTerm patternTerm) {
            if (slot instanceof SlotFake slotFake) {
                if (!slotFake.getDisplayStack().isEmpty()) {
                    if (randomComplement$getCraftables(patternTerm).contains(new CraftableItem(slotFake.getDisplayStack()))) {
                        MEHandler.drawPlus(slotFake);
                    }
                }
            }
        }
    }


}
