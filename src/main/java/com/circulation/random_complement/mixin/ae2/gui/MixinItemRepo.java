package com.circulation.random_complement.mixin.ae2.gui;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.me.ItemRepo;
import appeng.util.ItemSorters;
import appeng.util.Platform;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import com.circulation.random_complement.common.util.SimpleItem;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Comparator;
import java.util.List;

@Mixin(value = ItemRepo.class,remap = false)
public abstract class MixinItemRepo {

    @Shadow
    private static Comparator<IAEItemStack> getComparator(Enum<?> sortBy) {
        return ItemSorters.CONFIG_BASED_SORT_BY_MOD;
    }

    @Unique
    public int randomComplement$counter = 0;

    @Redirect(method = "updateView",at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V"))
    public void updateView(List<IAEItemStack> instance, Comparator<IAEItemStack> comparator) {
        Comparator<IAEItemStack> priorityComparator = (a, b) -> {
            boolean aPri = randomComplement$isPriorityItem(a);
            boolean bPri = randomComplement$isPriorityItem(b);
            return Boolean.compare(bPri, aPri);
        };
        instance.sort(priorityComparator.thenComparing(comparator));
    }

    @Unique
    private boolean randomComplement$isPriorityItem(IAEItemStack stack) {
        if (Platform.isClient() && Minecraft.getMinecraft().currentScreen instanceof GuiMEMonitorable g) {
            SimpleItem item = SimpleItem.getInstance(stack.getDefinition());
            var list = ((SpecialLogic) g).r$getList();
            return list.contains(item);
        }
        return false;
    }
}
