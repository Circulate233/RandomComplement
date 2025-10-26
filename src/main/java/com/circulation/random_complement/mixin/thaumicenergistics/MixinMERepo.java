package com.circulation.random_complement.mixin.thaumicenergistics;

import appeng.api.config.SortOrder;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.util.Platform;
import com.circulation.random_complement.client.RCAECraftablesGui;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumicenergistics.client.gui.helpers.MERepo;
import thaumicenergistics.client.gui.helpers.ThEItemSorters;

import java.util.ArrayList;
import java.util.Comparator;

@Mixin(value = MERepo.class, remap = false)
public class MixinMERepo<T extends IAEStack<T>> {

    @Shadow
    private ArrayList<T> view;
    @Unique
    private Comparator<IAEStack<?>> r$priorityComparator = (a, b) -> {
        boolean aPri = randomComplement$isPriorityItem(a);
        boolean bPri = randomComplement$isPriorityItem(b);
        return Boolean.compare(bPri, aPri);
    };

    @Shadow
    private static Comparator<IAEStack<?>> getComparator(SortOrder sortBy) {
        return ThEItemSorters.CONFIG_BASED_SORT_BY_MOD;
    }

    @Redirect(method = "updateView", at = @At(value = "INVOKE", target = "Lthaumicenergistics/client/gui/helpers/MERepo;getComparator(Lappeng/api/config/SortOrder;)Ljava/util/Comparator;"))
    public Comparator<IAEStack<?>> updateView(SortOrder c) {
        if (this.view.isEmpty()) {
            return getComparator(c);
        }
        if (this.view.get(0) instanceof IAEItemStack) {
            return r$priorityComparator.thenComparing(getComparator(c));
        }
        return getComparator(c);
    }

    @Unique
    private boolean randomComplement$isPriorityItem(IAEStack<?> stack) {
        if (Platform.isClient() && stack instanceof IAEItemStack s && Minecraft.getMinecraft().currentScreen instanceof RCAECraftablesGui g) {
            var list = g.r$getCpuCache();
            return list.contains(s);
        }
        return false;
    }
}
