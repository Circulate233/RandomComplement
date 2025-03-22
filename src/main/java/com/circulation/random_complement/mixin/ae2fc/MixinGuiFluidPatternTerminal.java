package com.circulation.random_complement.mixin.ae2fc;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.container.slot.SlotFake;
import com.circulation.random_complement.client.CraftableItem;
import com.circulation.random_complement.client.handler.MEHandler;
import com.circulation.random_complement.mixin.ae2.AccessorGuiMEMonitorable;
import com.glodblock.github.client.GuiFluidPatternTerminal;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mixin(value = GuiFluidPatternTerminal.class)
public class MixinGuiFluidPatternTerminal {

    @Unique
    private Set<CraftableItem> randomComplement$craftableCache = new HashSet<>();

    @Unique
    private Set<IAEItemStack> randomComplement$getStorage() {
        IItemList<IAEItemStack> all = ((AccessorGuiMEMonitorable)this).getRepo().getList();
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

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Slot;getStack()Lnet/minecraft/item/ItemStack;",ordinal = 0))
    private void drawSlotFake(Slot slot, CallbackInfo ci) {
        if (slot instanceof SlotFake slotFake) {
            if (!slotFake.getDisplayStack().isEmpty()) {
                var item = slotFake.getDisplayStack();
                if (randomComplement$getCraftables().contains(new CraftableItem(item))) {
                    MEHandler.drawPlus(slotFake);
                } else if (item.getItem() instanceof ItemFluidPacket){
                    var item1 = FakeFluids.packFluid2Drops(((IAEFluidStack)FakeItemRegister.getAEStack(item)).getFluidStack());
                    if (randomComplement$getCraftables().contains(new CraftableItem(item1))) {
                        MEHandler.drawPlus(slotFake);
                    }
                } else if (Loader.isModLoaded("mekeng")){
                    randomComplement$mekengDrawSlot(item,slot);
                }

            }
        }
    }

    @Unique
    @Optional.Method(modid = "mekeng")
    private void randomComplement$mekengDrawSlot(ItemStack item,Slot slot){
        if (item.getItem() instanceof ItemGasPacket) {
            var item1 = FakeGases.packGas2Drops(((IAEGasStack) FakeItemRegister.getAEStack(item)).getGasStack());
            if (randomComplement$getCraftables().contains(new CraftableItem(item1))) {
                MEHandler.drawPlus(slot);
            }
        }
    }
}
