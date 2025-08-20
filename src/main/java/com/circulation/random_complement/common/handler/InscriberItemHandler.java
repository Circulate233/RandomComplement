package com.circulation.random_complement.common.handler;

import appeng.api.AEApi;
import appeng.api.definitions.IItemDefinition;
import appeng.api.features.IInscriberRecipe;
import appeng.api.features.IInscriberRegistry;
import appeng.tile.misc.TileInscriber;
import appeng.util.Platform;
import appeng.util.inv.filter.IAEItemFilter;
import com.circulation.random_complement.common.interfaces.ItemHandlerTool;
import com.circulation.random_complement.common.util.SimpleItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InscriberItemHandler implements IAEItemFilter {
    private final TileInscriber te;
    private List<IItemHandler> itemHandlers;
    private final static IInscriberRegistry reg = AEApi.instance().registries().inscriber();
    private final static Map<SimpleItem,Set<SimpleRecipe>> recipes = new ConcurrentHashMap<>();
    private final static Map<SimpleItem,Set<SimpleRecipe>> recipesi = new ConcurrentHashMap<>();

    private final static IItemDefinition namePress = AEApi.instance().definitions().materials().namePress();

    static {
        reg.getRecipes().forEach(recipe -> {
            var sr = new SimpleRecipe(recipe);
            sr.top.forEach(top -> {
                if (recipes.get(top) == null) {
                    Set<SimpleRecipe> list = new HashSet<>();
                    list.add(sr);
                    recipes.put(top,list);
                } else {
                    recipes.get(top).add(sr);
                }
            });
            sr.button.forEach(button -> {
                if (recipes.get(button) == null) {
                    Set<SimpleRecipe> list = new HashSet<>();
                    list.add(sr);
                    recipes.put(button,list);
                } else {
                    recipes.get(button).add(sr);
                }
            });
            sr.input.forEach(input -> {
                if (recipesi.get(input) == null) {
                    Set<SimpleRecipe> list = new HashSet<>();
                    list.add(sr);
                    recipesi.put(input,list);
                } else {
                    recipesi.get(input).add(sr);
                }
            });
        });
    }

    public InscriberItemHandler(TileInscriber te) {
        this.te = te;
    }

    public boolean allowExtract(IItemHandler inv, int slot, int amount) {
        return slot == 1;
    }

    public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
        if (itemHandlers == null){
            this.itemHandlers = ((ItemHandlerTool)te).r$getItemHandlers();
        }
        if (slot == 1) {
            return false;
        } else if (te.isSmash()) {
            return false;
        } else if (this.hasNamePress(itemHandlers)) {
            return inv == itemHandlers.get(2);
        } else if (namePress.isSameAs(stack)) {
            return inv != itemHandlers.get(2);
        } else {
            final Set<ItemStack> inputs;
            final boolean isEmpty = isEmpty(itemHandlers);
            if (isEmpty) {
                if (inv == itemHandlers.get(2))
                    inputs = reg.getInputs();
                else
                    inputs = reg.getOptionals();
                for (ItemStack input : inputs) {
                    if (Platform.itemComparisons().isSameItem(stack, input)) {
                        return true;
                    }
                }
            } else {
                final var allSimpleItem = allSlotSimpleItem(itemHandlers);
                return this.getUsableItems(allSimpleItem, inv).contains(SimpleItem.getInstance(stack));
            }
            return false;
        }
    }

    public boolean hasNamePress(List<IItemHandler> handlers) {
        ItemStack slot0 = handlers.get(0).getStackInSlot(0);
        ItemStack slot1 = handlers.get(1).getStackInSlot(0);
        return namePress.isSameAs(slot0) || namePress.isSameAs(slot1);
    }

    public List<SimpleItem> allSlotSimpleItem(List<IItemHandler> itemHandlers){
        List<SimpleItem> list = new ArrayList<>();
        itemHandlers.forEach(itemHandler -> list.add(SimpleItem.getInstance(itemHandler.getStackInSlot(0))));
        return list;
    }

    public List<ItemStack> allSlotItem(List<IItemHandler> itemHandlers){
        List<ItemStack> list = new ArrayList<>();
        itemHandlers.forEach(itemHandler -> list.add(itemHandler.getStackInSlot(0)));
        return list;
    }

    public boolean isEmpty(List<IItemHandler> handlers) {
        return handlers.stream().allMatch(h -> h.getStackInSlot(0).isEmpty());
    }

    private static class SimpleRecipe{
        private final Set<SimpleItem> input;
        private final Set<SimpleItem> top;
        private final Set<SimpleItem> button;
        private final boolean isTwo;

        public SimpleRecipe(IInscriberRecipe recipe){
            Set<SimpleItem> input = new HashSet<>();
            recipe.getInputs().forEach(item -> input.add(SimpleItem.getInstance(item)));
            this.input = input;
            Set<SimpleItem> top = new HashSet<>();
            recipe.getTopInputs().forEach(item -> top.add(SimpleItem.getInstance(item)));
            this.top = top;
            Set<SimpleItem> button = new HashSet<>();
            recipe.getBottomInputs().forEach(item -> button.add(SimpleItem.getInstance(item)));
            this.button = button;

            this.isTwo = input.isEmpty() || button.isEmpty();
        }

        public Set<SimpleItem> getCounter(SimpleItem item){
            if (top.contains(item)){
                return button;
            } else if (button.contains(item)) {
                return top;
            } else {
                return Collections.emptySet();
            }
        }
    }

    public Set<SimpleItem> getUsableItems(List<SimpleItem> slots, IItemHandler inv){
        final Set<SimpleItem> out = new HashSet<>();
        final var item = slots.get(0).isEmpty() ? slots.get(1) : slots.get(0);
        final var slot2 = slots.get(2);
        if (slot2.isEmpty()){
            if (inv == itemHandlers.get(2)) {
                if (recipes.get(item) == null){
                    return Collections.emptySet();
                }
                if (slots.get(0).isEmpty() || slots.get(1).isEmpty()){
                    recipes.get(item).forEach(recipe -> {
                        if (recipe.top.contains(item) || recipe.button.contains(item)) {
                            out.addAll(recipe.input);
                        }
                    });
                } else {
                    recipes.get(item).forEach(recipe -> {
                        if (recipe.top.contains(item) || recipe.button.contains(item)){
                            if (recipe.getCounter(item).contains(slots.get(1))) {
                                out.addAll(recipe.input);
                            }
                        }
                    });
                }
            } else {
                if (recipes.get(item) == null){
                    return Collections.emptySet();
                }
                recipes.get(item).forEach(recipe -> {
                    if (!recipe.isTwo) {
                        if (recipe.top.contains(item) || recipe.button.contains(item)) {
                            out.addAll(recipe.getCounter(item));
                        }
                    }
                });
            }
        } else {
            if (item.isEmpty()) {
                if (recipesi.get(slot2) == null){
                    return Collections.emptySet();
                }
                recipesi.get(slot2).forEach(recipe -> {
                    if (recipe.input.contains(slot2)) {
                        out.addAll(recipe.getCounter(item));
                    }
                });
            } else {
                if (recipes.get(item) == null){
                    return Collections.emptySet();
                }
                recipes.get(item).forEach(recipe -> {
                    if (!recipe.isTwo) {
                        if (recipe.input.contains(slot2)) {
                            if (recipe.top.contains(item) || recipe.button.contains(item)) {
                                out.addAll(recipe.getCounter(item));
                            }
                        }
                    }
                });
            }
        }
        return out;
    }
}
