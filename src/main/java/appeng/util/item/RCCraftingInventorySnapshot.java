package appeng.util.item;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.util.inv.ItemListIgnoreCrafting;
import com.circulation.random_complement.mixin.ae2.AccessorItemList;
import com.circulation.random_complement.mixin.ae2.AccessorItemListIgnoreCrafting;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.item.Item;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class RCCraftingInventorySnapshot {
    private RCCraftingInventorySnapshot() {
    }

    public static boolean tryCopyInto(IItemList<IAEItemStack> source, IItemList<IAEItemStack> cache) {
        if (!(source instanceof ItemList) || !(source instanceof AccessorItemList)
            || cache.getClass() != ItemListIgnoreCrafting.class || !(cache instanceof AccessorItemListIgnoreCrafting)) {
            return false;
        }
        IItemList<?> target = ((AccessorItemListIgnoreCrafting) cache).rc$getTarget();
        if (!(target instanceof ItemList) || !(target instanceof AccessorItemList)) return false;
        @SuppressWarnings("unchecked")
        Reference2ObjectMap<Item, ItemVariantList> sourceRecords =
            (Reference2ObjectMap<Item, ItemVariantList>) ((AccessorItemList) source).rc$getRecords();
        @SuppressWarnings("unchecked")
        Reference2ObjectMap<Item, ItemVariantList> targetRecords =
            (Reference2ObjectMap<Item, ItemVariantList>) ((AccessorItemList) target).rc$getRecords();
        if (sourceRecords.getClass() != Reference2ObjectOpenHashMap.class
            || targetRecords.getClass() != Reference2ObjectOpenHashMap.class
            || !targetRecords.isEmpty()) {
            return false;
        }
        AtomicInteger version = ((AccessorItemList) source).rc$getVersion();
        int expected = version.get();
        for (ItemVariantList bucket : sourceRecords.values()) {
            if (bucket.getClass() == FuzzyItemVariantList.class) continue;
            if ((bucket.getClass() != NormalItemVariantList.class && bucket.getClass() != NormalBucket.class)
                || bucket.getRecords().getClass() != Reference2ObjectOpenHashMap.class) {
                return false;
            }
        }
        targetRecords.putAll(sourceRecords);
        ObjectSet<Reference2ObjectMap.Entry<Item, ItemVariantList>> entries = targetRecords.reference2ObjectEntrySet();
        ObjectIterator<Reference2ObjectMap.Entry<Item, ItemVariantList>> buckets =
            entries instanceof Reference2ObjectMap.FastEntrySet
                ? ((Reference2ObjectMap.FastEntrySet<Item, ItemVariantList>) entries).fastIterator()
                : entries.iterator();
        while (buckets.hasNext()) {
            Reference2ObjectMap.Entry<Item, ItemVariantList> entry = buckets.next();
            ItemVariantList sourceBucket = entry.getValue();
            if (sourceBucket.getClass() != FuzzyItemVariantList.class) {
                NormalBucket copied = copyNormalTable(sourceBucket, version, expected);
                if (copied.records.isEmpty()) buckets.remove();
                else entry.setValue(copied);
                continue;
            }
            Iterator<IAEItemStack> stacks = sourceBucket.iterator();
            if (!stacks.hasNext()) {
                buckets.remove();
                continue;
            }
            ItemVariantList targetBucket = new FuzzyItemVariantList();
            Map<AESharedItemStack, IAEItemStack> targetVariants = targetBucket.getRecords();
            while (stacks.hasNext()) {
                if (version.get() != expected) throw new ConcurrentModificationException();
                IAEItemStack copied = stacks.next().copy();
                copied.setCraftable(false);
                targetVariants.put(((AEItemStack) copied).getSharedStack(), copied);
            }
            entry.setValue(targetBucket);
        }
        if (version.get() != expected) throw new ConcurrentModificationException();
        return true;
    }

    private static NormalBucket copyNormalTable(ItemVariantList source, AtomicInteger version, int expected) {
        Reference2ObjectOpenHashMap<AESharedItemStack, IAEItemStack> table =
            ((Reference2ObjectOpenHashMap<AESharedItemStack, IAEItemStack>) source.getRecords()).clone();
        ObjectIterator<Reference2ObjectMap.Entry<AESharedItemStack, IAEItemStack>> it =
            table.reference2ObjectEntrySet().fastIterator();
        boolean needsCleanup = false;
        while (it.hasNext()) {
            if (version.get() != expected) throw new ConcurrentModificationException();
            Reference2ObjectMap.Entry<AESharedItemStack, IAEItemStack> entry = it.next();
            IAEItemStack stack = entry.getValue();
            if (!stack.isMeaningful()) {
                it.remove();
                needsCleanup = true;
                continue;
            }
            IAEItemStack copy = stack.copy();
            copy.setCraftable(false);
            entry.setValue(copy);
        }
        if (needsCleanup) {
            for (Iterator<IAEItemStack> remaining = source.iterator(); remaining.hasNext();) remaining.next();
        }
        return new NormalBucket(table);
    }

    private static final class NormalBucket extends ItemVariantList {
        private final Reference2ObjectMap<AESharedItemStack, IAEItemStack> records;

        private NormalBucket(Reference2ObjectOpenHashMap<AESharedItemStack, IAEItemStack> table) {
            records = table;
        }

        @Override
        Map<AESharedItemStack, IAEItemStack> getRecords() {
            return records;
        }

        @Override
        public Collection<IAEItemStack> findFuzzy(IAEItemStack filter, FuzzyMode fuzzy) {
            return records.values();
        }
    }
}
