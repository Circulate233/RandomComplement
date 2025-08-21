package com.circulation.random_complement.mixin.ae2.gui;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.me.ItemRepo;
import appeng.util.Platform;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import com.circulation.random_complement.common.util.SimpleItem;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Comparator;
import java.util.List;

@Mixin(value = ItemRepo.class,remap = false)
public abstract class MixinItemRepo {

    @Unique
    private final Comparator<IAEItemStack> randomComplement$priorityComparator = (a, b) -> {
        boolean aPri = randomComplement$isPriorityItem(a);
        boolean bPri = randomComplement$isPriorityItem(b);
        if (aPri == bPri){
            return 0;
        } else {
            return aPri ? -1 : 1;
        }
    };

    @Redirect(method = "updateView",at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V"))
    public void updateView(List<IAEItemStack> instance, Comparator<IAEItemStack> comparator) {
        instance.sort(comparator);
        instance.sort(randomComplement$priorityComparator);
    }

    @Unique
    private boolean randomComplement$isPriorityItem(IAEItemStack stack) {
        if (Platform.isClient() && Minecraft.getMinecraft().currentScreen instanceof SpecialLogic g) {
            SimpleItem item = SimpleItem.getInstance(stack.getDefinition());
            var list = g.r$getList();
            return list.contains(item);
        }
        return false;
    }
}
