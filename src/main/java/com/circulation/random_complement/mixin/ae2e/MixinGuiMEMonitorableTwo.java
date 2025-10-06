package com.circulation.random_complement.mixin.ae2e;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.AEBaseMEGui;
import appeng.client.me.ItemRepo;
import com._0xc4de.ae2exttable.client.gui.GuiMEMonitorableTwo;
import com.circulation.random_complement.common.handler.MEHandler;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;
import java.util.Set;

@Mixin(value = GuiMEMonitorableTwo.class)
public abstract class MixinGuiMEMonitorableTwo extends AEBaseMEGui implements SpecialLogic {

    @Shadow(remap = false)
    @Final
    protected ItemRepo repo;

    @Unique
    public final Set<IAEItemStack> randomComplement$craftableCache = new ObjectOpenHashSet<>();

    @Unique
    private final Set<IAEItemStack> randomComplement$mergedCache = new ObjectOpenHashSet<>();

    public MixinGuiMEMonitorableTwo(Container container) {
        super(container);
    }

    @Unique
    @Override
    public Set<IAEItemStack> r$getList() {
        if (randomComplement$mergedCache.isEmpty()){
            randomComplement$mergedCache.addAll(MEHandler.getCraftableCacheS());
            randomComplement$mergedCache.addAll(randomComplement$craftableCache);
            MEHandler.getCraftableCacheS().clear();
        }
        return randomComplement$mergedCache;
    }

    @Unique
    @Override
    public void r$setList(Collection<IAEItemStack> list) {
        randomComplement$craftableCache.clear();
        randomComplement$craftableCache.addAll(list);
    }

    @Unique
    @Override
    public void r$addAllList(Collection<IAEItemStack> list) {
        randomComplement$craftableCache.addAll(list);
    }

    @Unique
    @Override
    public ItemRepo r$getRepo(){
        return this.repo;
    }

}
