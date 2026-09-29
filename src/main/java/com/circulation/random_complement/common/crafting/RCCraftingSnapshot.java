package com.circulation.random_complement.common.crafting;

import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.util.Platform;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Set;

public final class RCCraftingSnapshot {

    public static final int MAX_ITEMS = 4096;
    public static final int MAX_SUBSTITUTES = 256;
    public static final int MAX_STEPS = 8192;
    public static final int TRIM_COST_FACTOR = 6;
    public static final long BUILD_BUDGET_NANOS = 15_000_000L;

    private static final Keys UNUSABLE = new Keys(null, null);

    private RCCraftingSnapshot() {
    }

    public static Keys rc$build(ICraftingGrid grid, IAEItemStack output, World world) {
        if (grid == null || output == null || world == null) {
            return UNUSABLE;
        }
        long deadline = System.nanoTime() + BUILD_BUDGET_NANOS;
        Set<IAEItemStack> keys = new ObjectOpenHashSet<>();
        Set<IAEItemStack> variantKeys = new ObjectOpenHashSet<>();
        Deque<IAEItemStack> pending = new ArrayDeque<>();
        keys.add(output);
        pending.add(output);
        int steps = 0;
        try {
            while (!pending.isEmpty()) {
                if (System.nanoTime() > deadline) {
                    return UNUSABLE;
                }
                IAEItemStack current = pending.poll();
                Collection<ICraftingPatternDetails> details = grid.getCraftingFor(current, null, 0, world);
                if (details == null) {
                    continue;
                }
                for (ICraftingPatternDetails pattern : details) {
                    if (pattern == null) {
                        continue;
                    }
                    if (++steps > MAX_STEPS) {
                        return UNUSABLE;
                    }
                    IAEItemStack[] outputs = pattern.getCondensedOutputs();
                    if (outputs == null) {
                        continue;
                    }
                    long produced = 0;
                    for (IAEItemStack out : outputs) {
                        if (out != null && out.equals(current)) {
                            produced += out.getStackSize();
                        }
                    }
                    if (produced <= 0) {
                        continue;
                    }
                    IAEItemStack[] inputs = pattern.getCondensedInputs();
                    if (inputs == null) {
                        continue;
                    }
                    List<IAEItemStack> substitutes = rc$substitutes(pattern);
                    for (IAEItemStack input : inputs) {
                        if (input == null || input.getStackSize() <= 0) {
                            continue;
                        }
                        if (substitutes != null) {
                            variantKeys.add(input);
                        }
                        if (keys.add(input)) {
                            if (keys.size() > MAX_ITEMS) {
                                return UNUSABLE;
                            }
                            pending.add(input);
                        }
                    }
                    if (substitutes != null) {
                        for (IAEItemStack substitute : substitutes) {
                            if (keys.add(substitute) && keys.size() > MAX_ITEMS) {
                                return UNUSABLE;
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            return UNUSABLE;
        }
        return new Keys(keys, variantKeys.isEmpty() ? null : variantKeys);
    }

    public static IItemList<IAEItemStack> trim(IItemList<IAEItemStack> full, Keys keys, int fullSize) {
        if (full == null || keys == null || !keys.rc$isUsable()) {
            return full;
        }
        Set<IAEItemStack> wanted = keys.rc$keys();
        if (wanted.isEmpty() || fullSize <= 0) {
            return full;
        }
        if (wanted.size() * TRIM_COST_FACTOR >= fullSize) {
            return full;
        }
        Set<IAEItemStack> variantKeys = keys.rc$variantKeys();
        int planned = 0;
        for (IAEItemStack key : wanted) {
            if (key == null) {
                continue;
            }
            if (rc$readsVariants(key, variantKeys)) {
                planned += full.findFuzzy(key, FuzzyMode.IGNORE_ALL).size();
            } else if (full.findPrecise(key) != null) {
                ++planned;
            }
            if (planned * TRIM_COST_FACTOR >= fullSize) {
                return full;
            }
        }
        if (planned <= 0) {
            return full;
        }

        IItemList<IAEItemStack> trimmed = AEApi.instance().storage()
            .getStorageChannel(IItemStorageChannel.class).createList();

        for (IAEItemStack key : wanted) {
            if (key == null) {
                continue;
            }
            if (rc$readsVariants(key, variantKeys)) {
                for (IAEItemStack variant : full.findFuzzy(key, FuzzyMode.IGNORE_ALL)) {
                    if (variant == null || variant.getStackSize() <= 0) {
                        continue;
                    }
                    if (trimmed.findPrecise(variant) != null) {
                        continue;
                    }
                    trimmed.addStorage(variant);
                }
            } else {
                IAEItemStack precise = full.findPrecise(key);
                if (precise == null || precise.getStackSize() <= 0) {
                    continue;
                }
                if (trimmed.findPrecise(precise) != null) {
                    continue;
                }
                trimmed.addStorage(precise);
            }
        }

        if (trimmed.size() >= fullSize) {
            return full;
        }
        return trimmed;
    }

    private static List<IAEItemStack> rc$substitutes(ICraftingPatternDetails pattern) {
        if (!pattern.canSubstitute()) {
            return null;
        }
        IAEItemStack[] slots = pattern.getInputs();
        if (slots == null || slots.length == 0) {
            return null;
        }
        Set<IAEItemStack> collected = null;
        for (int slot = 0; slot < slots.length; slot++) {
            List<IAEItemStack> slotSubstitutes = pattern.getSubstituteInputs(slot);
            if (slotSubstitutes == null || slotSubstitutes.isEmpty()) {
                continue;
            }
            if (collected == null) {
                collected = new ObjectOpenHashSet<>();
            }
            for (IAEItemStack substitute : slotSubstitutes) {
                if (substitute == null || substitute.getStackSize() <= 0) {
                    continue;
                }
                if (collected.add(substitute) && collected.size() > MAX_SUBSTITUTES) {
                    throw new IllegalStateException("too many substitutes");
                }
            }
        }
        return collected == null ? null : new ArrayList<>(collected);
    }

    private static boolean rc$readsVariants(IAEItemStack stack, Set<IAEItemStack> variantKeys) {
        if (variantKeys != null && variantKeys.contains(stack)) {
            return true;
        }
        Item item = stack.getDefinition().getItem();
        return item.getHasSubtypes() || Platform.isGTDamageableItem(item);
    }

    public static final class Keys {

        private final Set<IAEItemStack> keys;
        private final Set<IAEItemStack> variantKeys;

        private Keys(Set<IAEItemStack> keys, Set<IAEItemStack> variantKeys) {
            this.keys = keys == null ? null : Collections.unmodifiableSet(keys);
            this.variantKeys = variantKeys == null ? null : Collections.unmodifiableSet(variantKeys);
        }

        public boolean rc$isUsable() {
            return this.keys != null;
        }

        public Set<IAEItemStack> rc$keys() {
            return this.keys;
        }

        public Set<IAEItemStack> rc$variantKeys() {
            return this.variantKeys;
        }

        public int rc$size() {
            return this.keys == null ? 0 : this.keys.size();
        }
    }
}
