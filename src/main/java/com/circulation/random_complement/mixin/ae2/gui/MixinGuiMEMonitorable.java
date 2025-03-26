package com.circulation.random_complement.mixin.ae2.gui;

import appeng.client.gui.implementations.GuiMEMonitorable;
import com.circulation.random_complement.client.CraftableItem;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = GuiMEMonitorable.class)
public class MixinGuiMEMonitorable implements SpecialLogic {

    @Unique
    private static final Set<CraftableItem> randomComplement$craftableCacheS = new HashSet<>();

    @Unique
    public Set<CraftableItem> randomComplement$craftableCache = new HashSet<>();

    @Inject(method = "onGuiClosed",at = @At("TAIL"))
    public void onGuiClosedMixin(CallbackInfo ci) {
        randomComplement$craftableCacheS.clear();
        randomComplement$craftableCacheS.addAll(randomComplement$craftableCache);
    }

    @Unique
    @Override
    public Set<CraftableItem> r$getList() {
        Set<CraftableItem> set = new HashSet<>(randomComplement$craftableCacheS);
        set.addAll(randomComplement$craftableCache);
        return set;
    }

    @Unique
    @Override
    public void r$setList(Set<CraftableItem> list) {
        randomComplement$craftableCache = list;
    }

    @Override
    public void r$addList(CraftableItem item) {
        randomComplement$craftableCache.add(item);
    }

    @Override
    public void r$addAllList(Set<CraftableItem> list) {
        randomComplement$craftableCache.addAll(list);
    }

}
