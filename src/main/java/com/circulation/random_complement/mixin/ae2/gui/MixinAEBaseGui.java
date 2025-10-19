package com.circulation.random_complement.mixin.ae2.gui;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.AEBaseGui;
import appeng.client.me.SlotME;
import appeng.container.slot.SlotFake;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.client.handler.RCInputHandler;
import com.circulation.random_complement.common.handler.MEHandler;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.circulation.random_complement.RCConfig.AE2;

@Mixin(value = AEBaseGui.class)
public abstract class MixinAEBaseGui extends GuiContainer {

    @Unique
    private final List<Slot> r$plusSlot = new ObjectArrayList<>();
    @Unique
    private final int randomComplement$textureIndex = AE2.craftingSlotTextureIndex;
    @Unique
    private Set<IAEItemStack> randomComplement$craftableCache;

    public MixinAEBaseGui(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Unique
    protected List<Slot> r$getPlusSlot() {
        return r$plusSlot;
    }

    @Shadow(remap = false)
    protected abstract List<Slot> getInventorySlots();

    @Unique
    private Set<IAEItemStack> randomComplement$getStorage(SpecialLogic gui) {
        var repo = gui.r$getRepo();
        IItemList<IAEItemStack> all = repo != null ? repo.getList() : null;
        return all == null ? ObjectSets.emptySet() : StreamSupport.stream(all.spliterator(), false).collect(Collectors.toCollection(ObjectOpenHashSet::new));
    }

    @Unique
    private Set<IAEItemStack> randomComplement$getCraftables(SpecialLogic gui) {
        if (randomComplement$craftableCache == null) {
            randomComplement$craftableCache = this.randomComplement$getStorage(gui)
                    .stream()
                    .filter(IAEStack::isCraftable)
                    .collect(Collectors.toCollection(ObjectOpenHashSet::new));
        }

        return randomComplement$craftableCache;
    }

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lappeng/client/render/StackSizeRenderer;renderStackSize(Lnet/minecraft/client/gui/FontRenderer;Lappeng/api/storage/data/IAEItemStack;II)V", shift = At.Shift.AFTER, ordinal = 0, remap = false))
    private void drawSlotME(Slot slot, CallbackInfo ci) {
        if (slot instanceof SlotME slotME) {
            var aeStack = slotME.getAEStack();
            if (aeStack != null && aeStack.isCraftable()) {
                r$plusSlot.add(slotME);
            }
        }
    }

    @Inject(method = "drawSlot", at = @At(value = "HEAD"))
    private void drawSlotFake(Slot slot, CallbackInfo ci) {
        if (slot.xPos < 0 || slot.yPos < 0) return;
        if (Minecraft.getMinecraft().currentScreen instanceof SpecialLogic patternTerm) {
            if (patternTerm.r$notMonitorable()) return;
            if (slot instanceof SlotFake slotFake) {
                if (!slotFake.getDisplayStack().isEmpty()) {
                    if (randomComplement$getCraftables(patternTerm).contains(AEItemStack.fromItemStack(slotFake.getDisplayStack()))) {
                        r$plusSlot.add(slotFake);
                    }
                }
            }
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V", shift = At.Shift.AFTER))
    public void drawPlusSlot(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GlStateManager.translate(this.guiLeft, this.guiTop, 0);
        MEHandler.drawSlotPluses(r$getPlusSlot());
        GlStateManager.translate(-this.guiLeft, -this.guiTop, 0);
    }

    @Inject(method = "drawGuiContainerBackgroundLayer", at = @At(value = "INVOKE", target = "Lappeng/client/gui/AEBaseGui;drawBG(IIII)V", remap = false, shift = At.Shift.AFTER))
    private void drawPin(float f, int x, int y, CallbackInfo ci) {
        if ((Object) this instanceof SpecialLogic monitorable) {
            if (monitorable.r$notMonitorable()) return;
            var items = monitorable.r$getList();
            if (!items.isEmpty()) {
                List<SlotME> slots = new ObjectArrayList<>();
                for (Slot slot : this.getInventorySlots()) {
                    if (slot instanceof SlotME slotME) {
                        if (items.contains(slotME.getAEStack())) {
                            slots.add(slotME);
                        } else {
                            break;
                        }
                    }
                }

                final int cycle = (slots.size() + 8) / 9;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                MEHandler.bindTexture(this.mc, randomComplement$textureIndex);
                for (int i = 0; i < cycle; i++) {
                    int amount = Math.min(slots.size() - i * 9, 9);
                    int yOffset = (randomComplement$textureIndex < 3 || randomComplement$textureIndex == 6)
                            ? RCInputHandler.getCounter() * 18
                            : (randomComplement$textureIndex - 3) * 18;

                    this.drawTexturedModalRect(
                            this.getGuiLeft() + 8,
                            this.getGuiTop() + 17 + 18 * i,
                            0,
                            yOffset,
                            18 * amount, 18
                    );
                }
            }
        }
    }

}