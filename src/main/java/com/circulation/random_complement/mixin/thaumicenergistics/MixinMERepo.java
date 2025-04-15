package com.circulation.random_complement.mixin.thaumicenergistics;

import appeng.api.config.SortOrder;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.util.Platform;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import com.circulation.random_complement.common.util.SimpleItem;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumicenergistics.client.gui.helpers.MERepo;
import thaumicenergistics.client.gui.helpers.ThEItemSorters;
import thaumicenergistics.client.gui.part.GuiArcaneTerminal;

import java.util.Comparator;

@Mixin(value = MERepo.class,remap = false)
public class MixinMERepo {

    @Shadow
    private static Comparator<IAEStack<?>> getComparator(SortOrder sortBy) {
        return ThEItemSorters.CONFIG_BASED_SORT_BY_MOD;
    }

    @Redirect(method = "updateView",at = @At(value = "INVOKE", target = "Lthaumicenergistics/client/gui/helpers/MERepo;getComparator(Lappeng/api/config/SortOrder;)Ljava/util/Comparator;"))
    public Comparator<IAEItemStack> updateView(SortOrder c) {
        Comparator<IAEItemStack> priorityComparator = (a, b) -> {
            boolean aPri = randomComplement$isPriorityItem(a);
            boolean bPri = randomComplement$isPriorityItem(b);
            return Boolean.compare(bPri, aPri);
        };
        return priorityComparator.thenComparing(getComparator(c));
    }

    @Unique
    private boolean randomComplement$isPriorityItem(IAEItemStack stack) {
        if (Platform.isClient() && Minecraft.getMinecraft().currentScreen instanceof GuiArcaneTerminal g) {
            SimpleItem item = SimpleItem.getInstance(stack.getDefinition());
            var list = ((SpecialLogic) g).r$getList();
            return list.contains(item);
        }
        return false;
    }
}
