package com.circulation.random_complement.mixin.ae2e;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.AEBaseMEGui;
import appeng.client.me.ItemRepo;
import com._0xc4de.ae2exttable.client.gui.GuiMEMonitorableTwo;
import com.circulation.random_complement.client.RCAECraftablesGui;
import com.circulation.random_complement.common.util.MEHandler;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;
import java.util.Set;

@Mixin(value = GuiMEMonitorableTwo.class)
public abstract class MixinGuiMEMonitorableTwo extends AEBaseMEGui implements RCAECraftablesGui {

    @Unique
    public final Set<IAEItemStack> randomComplement$cpuCache = new ObjectOpenHashSet<>();
    @Unique
    private final Set<IAEItemStack> randomComplement$mergedCache = new ObjectOpenHashSet<>();
    @Shadow(remap = false)
    @Final
    protected ItemRepo repo;
    @Unique
    private Set<IAEItemStack> randomComplement$craftableCache = new ObjectOpenHashSet<>();

    public MixinGuiMEMonitorableTwo(Container container) {
        super(container);
    }

    @Unique
    private Set<IAEItemStack> randomComplement$getStorage() {
        var repo = this.repo;
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

}
