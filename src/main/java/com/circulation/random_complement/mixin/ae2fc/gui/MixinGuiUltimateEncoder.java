package com.circulation.random_complement.mixin.ae2fc.gui;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.slot.SlotFake;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.RCGuiButton;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.PatternTermAutoFillPattern;
import com.circulation.random_complement.common.interfaces.PatternTermConfigs;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import com.circulation.random_complement.common.network.RCConfigButton;
import com.circulation.random_complement.mixin.ae2.gui.MixinAEBaseGui;
import com.glodblock.github.client.GuiUltimateEncoder;
import com.glodblock.github.client.container.ContainerUltimateEncoder;
import com.glodblock.github.common.item.ItemFluidPacket;
import com.glodblock.github.common.item.ItemGasPacket;
import com.glodblock.github.common.item.fake.FakeFluids;
import com.glodblock.github.common.item.fake.FakeItemRegister;
import com.glodblock.github.integration.mek.FakeGases;
import com.mekeng.github.common.me.data.IAEGasStack;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Set;

@Mixin(value = GuiUltimateEncoder.class, remap = false)
public abstract class MixinGuiUltimateEncoder extends MixinAEBaseGui implements SpecialLogic {

    @Unique
    public Set<IAEItemStack> randomComplement$craftableCache = new ObjectOpenHashSet<>();

    @Unique
    private RCGuiButton randomComplement$AutoFillPattern;

    @Final
    @Shadow
    private ContainerUltimateEncoder container;

    public MixinGuiUltimateEncoder(Container container) {
        super(container);
    }

    @Inject(method = "initGui", at = @At("TAIL"), remap = true)
    public void initGuiMixin(CallbackInfo ci) {
        int i = 0;
        this.buttonList.add(this.randomComplement$AutoFillPattern = new RCGuiButton(this.guiLeft - 18, this.guiTop + i++ * 20 + 8, RCSettings.PatternTermAutoFillPattern, PatternTermAutoFillPattern.CLOSE));
        this.randomComplement$AutoFillPattern.setEXMessage(I18n.format("gui.pattern_term.auto_fill_pattern.ex.text"));
    }

    @Inject(method = "drawFG", at = @At("HEAD"), remap = false)
    public void drawFGMixin(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.randomComplement$AutoFillPattern != null) {
            this.randomComplement$AutoFillPattern.set(((PatternTermConfigs) this.container).r$getAutoFillPattern());
        }
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"), remap = true)
    protected void actionPerformedMixin(GuiButton btn, CallbackInfo ci) {
        boolean backwards = Mouse.isButtonDown(1);
        if (btn == this.randomComplement$AutoFillPattern) {
            var option = this.randomComplement$AutoFillPattern.getRCSetting();
            RandomComplement.NET_CHANNEL.sendToServer(new RCConfigButton(option, backwards));
        }
    }

    @Inject(method = "drawSlot", at = @At(value = "HEAD"), remap = true)
    private void drawSlotFake(Slot slot, CallbackInfo ci) {
        if (!this.randomComplement$craftableCache.isEmpty() && slot instanceof SlotFake slotFake) {
            var item = slotFake.getDisplayStack();
            if (!item.isEmpty()) {
                if (this.randomComplement$craftableCache.contains(AEItemStack.fromItemStack(item))) {
                    r$getPlusSlot().add(slotFake);
                } else if (item.getItem() instanceof ItemFluidPacket) {
                    var item1 = FakeFluids.packFluid2Drops(((IAEFluidStack) FakeItemRegister.getAEStack(item)).getFluidStack());
                    if (this.randomComplement$craftableCache.contains(AEItemStack.fromItemStack(item1))) {
                        r$getPlusSlot().add(slotFake);
                    }
                } else if (Loader.isModLoaded("mekeng")) {
                    randomComplement$mekengDrawSlot(item, slot);
                }
            }
        }
    }

    @Unique
    @Optional.Method(modid = "mekeng")
    private void randomComplement$mekengDrawSlot(ItemStack item, Slot slot) {
        if (item.getItem() instanceof ItemGasPacket) {
            var item1 = FakeGases.packGas2Drops(((IAEGasStack) FakeItemRegister.getAEStack(item)).getGasStack());
            if (this.randomComplement$craftableCache.contains(AEItemStack.fromItemStack(item1))) {
                r$getPlusSlot().add(slot);
            }
        }
    }

    @Unique
    @Override
    public Set<IAEItemStack> r$getList() {
        return randomComplement$craftableCache;
    }

    @Unique
    @Override
    public void r$setList(Collection<IAEItemStack> list) {
        randomComplement$craftableCache.clear();
        randomComplement$craftableCache.addAll(list);
    }

    @Unique
    @Override
    public void r$addAllList(Collection<IAEItemStack> list) {
        randomComplement$craftableCache.addAll(list);
    }
}