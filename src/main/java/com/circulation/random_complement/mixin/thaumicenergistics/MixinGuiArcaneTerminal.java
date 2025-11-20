package com.circulation.random_complement.mixin.thaumicenergistics;

import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import com.circulation.random_complement.client.RCAECraftablesGui;
import com.circulation.random_complement.client.handler.RCInputHandler;
import com.circulation.random_complement.common.util.MEHandler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumicenergistics.client.gui.helpers.MERepo;
import thaumicenergistics.client.gui.part.GuiArcaneTerminal;
import thaumicenergistics.container.slot.SlotGhost;
import thaumicenergistics.container.slot.SlotME;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.circulation.random_complement.RCConfig.AE2;

@Mixin(GuiArcaneTerminal.class)
public abstract class MixinGuiArcaneTerminal extends MixinGuiAbstractTerminal<IAEItemStack, IItemStorageChannel> implements RCAECraftablesGui {

    @Unique
    private final int randomComplement$textureIndex = AE2.craftingSlotTextureIndex;

    @Unique
    private Set<IAEItemStack> randomComplement$cpuCache = new ObjectOpenHashSet<>();

    @Unique
    private Set<IAEItemStack> randomComplement$mergedCache = new ObjectOpenHashSet<>();

    @Unique
    private Set<IAEItemStack> randomComplement$craftableCache = new ObjectOpenHashSet<>();

    public MixinGuiArcaneTerminal(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Unique
    private Set<IAEItemStack> randomComplement$getStorage() {
        var repo = ((AccessorMERepo<IAEItemStack>) this.getRepo());
        if (repo == null) {
            return ObjectSets.emptySet();
        } else {
            IItemList<IAEItemStack> list = repo.getList();
            if (list.isEmpty()) return ObjectSets.emptySet();
            var out = new ObjectOpenHashSet<IAEItemStack>();
            for (var stack : list) {
                out.add(stack);
            }
            return out;
        }
    }

    @Unique
    @Override
    public Set<IAEItemStack> r$getCraftablesCache() {
        if (randomComplement$craftableCache.isEmpty()) {
            var s = this.randomComplement$getStorage();
            if (s.isEmpty()) return ObjectSets.emptySet();
            s.stream()
             .filter(IAEStack::isCraftable)
             .forEach(randomComplement$craftableCache::add);
        }

        return randomComplement$craftableCache;
    }

    @Inject(method = "drawGuiContainerBackgroundLayer", at = @At("TAIL"))
    private void drawPin(float f, int x, int y, CallbackInfo ci) {
        var items = this.r$getCpuCache();
        if (!items.isEmpty()) {
            List<SlotME<?>> slots = new ObjectArrayList<>();
            for (Slot slot : this.inventorySlots.inventorySlots) {
                if (slot instanceof SlotME<?> slotME) {
                    if (slotME.getAEStack() instanceof IAEItemStack stack && items.contains(stack)) {
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
                    this.getGuiLeft() + 7,
                    this.getGuiTop() + 17 + 18 * i,
                    0,
                    yOffset,
                    18 * amount, 18
                );
            }
        }
    }

    @Intrinsic
    public void drawSlot(@NotNull Slot slot) {
        super.drawSlot(slot);
        if (slot instanceof SlotME<?> slotME) {
            var aeStack = slotME.getAEStack();
            if (aeStack != null && aeStack.isCraftable()) {
                r$getPlusSlot().add(slot);
            }
        }
        if (slot instanceof SlotGhost slotG) {
            var aeStack = slotG.getStack();
            if (!aeStack.isEmpty()) {
                if (r$getCraftablesCache().contains(MEHandler.packAEItem(aeStack))) {
                    r$getPlusSlot().add(slotG);
                }
            }
        }
    }

    @Unique
    @Override
    public Set<IAEItemStack> r$getCpuCache() {
        if (randomComplement$mergedCache.isEmpty()) {
            randomComplement$mergedCache.addAll(MEHandler.getCraftableCacheS());
            randomComplement$mergedCache.addAll(randomComplement$cpuCache);
            MEHandler.getCraftableCacheS().clear();
        }
        return randomComplement$mergedCache;
    }

    @Unique
    @Override
    public void r$addCpuCache(Collection<IAEItemStack> list) {
        randomComplement$cpuCache.addAll(list);
    }

    @Mixin(value = MERepo.class, remap = false)
    public interface AccessorMERepo<T extends IAEStack<T>> {

        @Accessor
        IItemList<T> getList();

    }

}