package com.circulation.random_complement.mixin.ae2.gui;

import appeng.client.gui.implementations.GuiMEMonitorable;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import com.circulation.random_complement.common.util.SimpleItem;
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
    private static final Set<SimpleItem> randomComplement$craftableCacheS = new HashSet<>();

    @Unique
    public final Set<SimpleItem> randomComplement$craftableCache = new HashSet<>();

    @Unique
    private final Set<SimpleItem> randomComplement$mergedCache = new HashSet<>();

    @Inject(method = "onGuiClosed",at = @At("TAIL"))
    public void onGuiClosedMixin(CallbackInfo ci) {
        randomComplement$craftableCacheS.clear();
        randomComplement$craftableCacheS.addAll(randomComplement$craftableCache);
    }

    @Unique
    @Override
    public Set<SimpleItem> r$getList() {
        if (randomComplement$mergedCache.isEmpty()){
            randomComplement$mergedCache.addAll(randomComplement$craftableCacheS);
            randomComplement$mergedCache.addAll(randomComplement$craftableCache);
        }
        return randomComplement$mergedCache;
    }

    @Unique
    @Override
    public void r$setList(Set<SimpleItem> list) {
        randomComplement$craftableCache.clear();
        randomComplement$mergedCache.addAll(list);
    }

    @Unique
    @Override
    public void r$addList(SimpleItem item) {
        randomComplement$craftableCache.add(item);
    }

    @Unique
    @Override
    public void r$addAllList(Set<SimpleItem> list) {
        randomComplement$craftableCache.addAll(list);
    }

}
