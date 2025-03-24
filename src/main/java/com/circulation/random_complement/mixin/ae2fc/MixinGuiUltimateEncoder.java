package com.circulation.random_complement.mixin.ae2fc;

import appeng.container.slot.SlotFake;
import com.circulation.random_complement.client.CraftableItem;
import com.circulation.random_complement.common.handler.MEHandler;
import com.glodblock.github.client.GuiUltimateEncoder;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = GuiUltimateEncoder.class,remap = false)
public class MixinGuiUltimateEncoder {

    @Unique
    public Set<CraftableItem> randomComplement$craftableCache = new HashSet<>();

    @Inject(method = "drawSlot", at = @At(value = "HEAD"))
    private void drawSlotFake(Slot slot, CallbackInfo ci) {
        if (!this.randomComplement$craftableCache.isEmpty()) {
            if (slot instanceof SlotFake slotFake) {
                if (!slotFake.getDisplayStack().isEmpty()) {
                    var store = this.randomComplement$craftableCache;
                    var craft = new CraftableItem(slotFake.getDisplayStack());
                    if (store.contains(craft)) {
                        MEHandler.drawPlus(slotFake);
                    }
                }
            }
        }
    }

}
