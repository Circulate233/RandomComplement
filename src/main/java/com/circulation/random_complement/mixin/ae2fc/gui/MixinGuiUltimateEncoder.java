package com.circulation.random_complement.mixin.ae2fc.gui;

import appeng.api.storage.data.IAEFluidStack;
import appeng.container.slot.SlotFake;
import com.circulation.random_complement.common.handler.MEHandler;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import com.circulation.random_complement.common.util.SimpleItem;
import com.glodblock.github.client.GuiUltimateEncoder;
import com.glodblock.github.common.item.ItemFluidPacket;
import com.glodblock.github.common.item.ItemGasPacket;
import com.glodblock.github.common.item.fake.FakeFluids;
import com.glodblock.github.common.item.fake.FakeItemRegister;
import com.glodblock.github.integration.mek.FakeGases;
import com.mekeng.github.common.me.data.IAEGasStack;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = GuiUltimateEncoder.class,remap = false)
public class MixinGuiUltimateEncoder implements SpecialLogic {

    @Unique
    public Set<SimpleItem> randomComplement$craftableCache = new HashSet<>();

    @Inject(method = "drawSlot", at = @At(value = "HEAD"),remap = true)
    private void drawSlotFake(Slot slot, CallbackInfo ci) {
        if (!this.randomComplement$craftableCache.isEmpty() && slot instanceof SlotFake slotFake) {
            var item = slotFake.getDisplayStack();
            if (!item.isEmpty()) {
                if (this.randomComplement$craftableCache.contains(SimpleItem.getInstance(item))) {
                    MEHandler.drawPlus(slotFake);
                } else if (item.getItem() instanceof ItemFluidPacket) {
                    var item1 = FakeFluids.packFluid2Drops(((IAEFluidStack) FakeItemRegister.getAEStack(item)).getFluidStack());
                    if (this.randomComplement$craftableCache.contains(SimpleItem.getInstance(item1))) {
                        MEHandler.drawPlus(slotFake);
                    }
                } else if (Loader.isModLoaded("mekeng")) {
                    randomComplement$mekengDrawSlot(item, slot);
                }
            }
        }
    }

    @Unique
    @Optional.Method(modid = "mekeng")
    private void randomComplement$mekengDrawSlot(ItemStack item, Slot slot){
        if (item.getItem() instanceof ItemGasPacket) {
            var item1 = FakeGases.packGas2Drops(((IAEGasStack) FakeItemRegister.getAEStack(item)).getGasStack());
            if (this.randomComplement$craftableCache.contains(SimpleItem.getInstance(item1))) {
                MEHandler.drawPlus(slot);
            }
        }
    }

    @Unique
    @Override
    public Set<SimpleItem> r$getList() {
        return randomComplement$craftableCache;
    }

    @Unique
    @Override
    public void r$setList(Set<SimpleItem> list) {
        randomComplement$craftableCache.clear();
        randomComplement$craftableCache.addAll(list);
    }

    @Override
    public void r$addList(SimpleItem item) {
        randomComplement$craftableCache.add(item);
    }

    @Override
    public void r$addAllList(Set<SimpleItem> list) {
        randomComplement$craftableCache.addAll(list);
    }

}
