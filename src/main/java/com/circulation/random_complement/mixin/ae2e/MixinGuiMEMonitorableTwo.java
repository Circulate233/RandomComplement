package com.circulation.random_complement.mixin.ae2e;

import appeng.client.gui.AEBaseMEGui;
import com._0xc4de.ae2exttable.client.gui.GuiMEMonitorableTwo;
import com.circulation.random_complement.common.handler.MEHandler;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import com.circulation.random_complement.common.util.SimpleItem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(value = GuiMEMonitorableTwo.class)
public abstract class MixinGuiMEMonitorableTwo extends AEBaseMEGui implements SpecialLogic {

    @Unique
    public final Set<SimpleItem> randomComplement$craftableCache = new ObjectOpenHashSet<>();

    @Unique
    private final Set<SimpleItem> randomComplement$mergedCache = new ObjectOpenHashSet<>();

    public MixinGuiMEMonitorableTwo(Container container) {
        super(container);
    }

    @Unique
    @Override
    public Set<SimpleItem> r$getList() {
        if (randomComplement$mergedCache.isEmpty()){
            randomComplement$mergedCache.addAll(MEHandler.craftableCacheS);
            randomComplement$mergedCache.addAll(randomComplement$craftableCache);
            MEHandler.craftableCacheS.clear();
        }
        return randomComplement$mergedCache;
    }

    @Unique
    @Override
    public void r$setList(Set<SimpleItem> list) {
        randomComplement$craftableCache.clear();
        randomComplement$craftableCache.addAll(list);
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
