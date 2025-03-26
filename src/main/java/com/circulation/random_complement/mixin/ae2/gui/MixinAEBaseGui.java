package com.circulation.random_complement.mixin.ae2.gui;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.me.SlotME;
import appeng.container.slot.SlotFake;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.CraftableItem;
import com.circulation.random_complement.common.handler.MEHandler;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.circulation.random_complement.RCConfig.AE2;

@Mixin(value = AEBaseGui.class)
public abstract class MixinAEBaseGui extends GuiContainer {

    @Shadow(remap = false)
    protected abstract List<Slot> getInventorySlots();

    @Unique
    private Set<CraftableItem> randomComplement$craftableCache = new HashSet<>();

    public MixinAEBaseGui(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Unique
    private Set<IAEItemStack> randomComplement$getStorage(GuiMEMonitorable gui) {
        IItemList<IAEItemStack> all = ((AccessorGuiMEMonitorable) gui).getRepo().getList();
        return all == null ? Collections.emptySet() : StreamSupport.stream(all.spliterator(), false).collect(Collectors.toSet());
    }

    @Unique
    private Set<CraftableItem> randomComplement$getCraftables(GuiMEMonitorable gui) {
        if (randomComplement$craftableCache.isEmpty()) {
            randomComplement$craftableCache = this.randomComplement$getStorage(gui)
                    .stream()
                    .filter(IAEStack::isCraftable)
                    .map(itemStack -> CraftableItem.getInstance(itemStack.getDefinition()))
                    .collect(Collectors.toSet());
        }

        return randomComplement$craftableCache;
    }

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lappeng/client/render/StackSizeRenderer;renderStackSize(Lnet/minecraft/client/gui/FontRenderer;Lappeng/api/storage/data/IAEItemStack;II)V", shift = At.Shift.AFTER, ordinal = 0, remap = false))
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
        if (Minecraft.getMinecraft().currentScreen instanceof GuiMEMonitorable patternTerm) {
            if (slot instanceof SlotFake slotFake) {
                if (!slotFake.getDisplayStack().isEmpty()) {
                    if (randomComplement$getCraftables(patternTerm).contains(CraftableItem.getInstance(slotFake.getDisplayStack()))) {
                        MEHandler.drawPlus(slotFake);
                    }
                }
            }
        }
    }

    @Unique
    private int randomComplement$counter = 0;
    @Unique
    private int randomComplement$counter1 = 0;

    @Inject(method = "drawGuiContainerBackgroundLayer", at = @At(value = "INVOKE", target = "Lappeng/client/gui/AEBaseGui;drawBG(IIII)V",remap = false, shift = At.Shift.AFTER))
    private void drawPin(float f, int x, int y, CallbackInfo ci){
        if ((Object)this instanceof GuiMEMonitorable monitorable){
            var items = ((SpecialLogic)monitorable).r$getList();
            if (!items.isEmpty()) {
                int i = 0;
                for (Slot slot : this.getInventorySlots()) {
                    if (slot instanceof SlotME slotME) {
                        if (items.contains(CraftableItem.getInstance(slotME.getStack()))) {
                            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                            this.randomComplement$bindTexture();
                            this.drawTexturedModalRect(
                                    this.getGuiLeft() + slot.xPos - 1,
                                    this.getGuiTop() + slot.yPos - 1,
                                    AE2.craftingSlotTextureIndex * 18,
                                    randomComplement$counter * 18,
                                    18, 18
                            );
                        } else {
                            randomComplement$counter = (randomComplement$counter + ((++randomComplement$counter1 & 3) == 0 ? 1 : 0)) % 14;
                            break;
                        }
                    }
                }
            }
        }
    }

    @Unique
    private void randomComplement$bindTexture() {
        final ResourceLocation loc = new ResourceLocation(RandomComplement.MOD_ID + ":textures/gui/pinned.png");
        this.mc.getTextureManager().bindTexture(loc);
    }

}
