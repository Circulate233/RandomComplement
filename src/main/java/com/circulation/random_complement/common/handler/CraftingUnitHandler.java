package com.circulation.random_complement.common.handler;

import appeng.api.AEApi;
import appeng.block.crafting.BlockCraftingUnit;
import co.neeve.nae2.NAE2;
import com.circulation.random_complement.common.util.SimpleItem;
import dev.rlnt.extracpus.setup.ModBlocks;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import java.util.NoSuchElementException;

public class CraftingUnitHandler {

    private static final Object2ReferenceMap<SimpleItem, BlockCraftingUnit> CraftingUnitItemMap = new Object2ReferenceOpenHashMap<>();

    private static final Reference2ReferenceMap<Block, ItemStack> CraftingUnitBlockMap = new Reference2ReferenceOpenHashMap<>();

    private static final Reference2ReferenceMap<Block, BlockCraftingUnit> CraftingUnitBaseMap = new Reference2ReferenceOpenHashMap<>();

    private static final ReferenceSet<Block> CraftingUnitBaseSet = new ReferenceOpenHashSet<>();

    static {
        AEApi.instance().definitions().blocks().craftingUnit().maybeBlock().ifPresent(b -> {
            CraftingUnitBaseMap.defaultReturnValue((BlockCraftingUnit) b);
            CraftingUnitBaseSet.add(b);
        });
        CraftingUnitBlockMap.defaultReturnValue(ItemStack.EMPTY);
    }

    public static void addMatch(ItemStack item, Block block) {
        CraftingUnitItemMap.put(SimpleItem.getInstance(item), (BlockCraftingUnit) block);
        CraftingUnitBlockMap.put(block, item);
    }

    public static void addMatch(ItemStack item, Block block, BlockCraftingUnit craftingUnitBase) {
        addMatch(item, block);
        CraftingUnitBaseMap.put(block, craftingUnitBase);
        CraftingUnitBaseSet.add(craftingUnitBase);
    }

    public static boolean isReplaceable(ItemStack item, Block block) {
        return (CraftingUnitBaseSet.contains(block) || CraftingUnitBlockMap.containsKey(block))
                && (item == null || CraftingUnitItemMap.containsKey(SimpleItem.getInstance(item)));
    }

    public static BlockCraftingUnit getCraftingUnitBase(Block block) {
        return CraftingUnitBaseMap.get(block);
    }

    public static BlockCraftingUnit getMatchBlock(ItemStack item) {
        return CraftingUnitItemMap.get(SimpleItem.getInstance(item));
    }

    public static ItemStack getMatchItem(Block block) {
        var item = CraftingUnitBlockMap.get(block).copy();
        item.setCount(1);
        return item;
    }

    public static void register() {
        registerAE();
        if (Loader.isModLoaded("nae2")) registerNAE2();
        if (Loader.isModLoaded("extracpus")) registerEXCPU();
    }

    private static void registerAE() {
        var def = AEApi.instance().definitions();
        var materials = def.materials();
        var blocks = def.blocks();
        addMatch(materials.cell1kPart().maybeStack(1).orElse(ItemStack.EMPTY), blocks.craftingStorage1k().maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
        addMatch(materials.cell4kPart().maybeStack(1).orElse(ItemStack.EMPTY), blocks.craftingStorage4k().maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
        addMatch(materials.cell16kPart().maybeStack(1).orElse(ItemStack.EMPTY), blocks.craftingStorage16k().maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
        addMatch(materials.cell64kPart().maybeStack(1).orElse(ItemStack.EMPTY), blocks.craftingStorage64k().maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
        addMatch(materials.engProcessor().maybeStack(1).orElse(ItemStack.EMPTY), blocks.craftingAccelerator().maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
    }

    @Optional.Method(modid = "nae2")
    private static void registerNAE2() {
        var def = NAE2.definitions();
        var materials = def.materials();
        var blocks = def.blocks();
        addMatch(materials.cellPart256K().maybeStack(1).orElse(ItemStack.EMPTY), blocks.storageCrafting256K().maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
        addMatch(materials.cellPart1024K().maybeStack(1).orElse(ItemStack.EMPTY), blocks.storageCrafting1024K().maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
        addMatch(materials.cellPart4096K().maybeStack(1).orElse(ItemStack.EMPTY), blocks.storageCrafting4096K().maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
        addMatch(materials.cellPart16384K().maybeStack(1).orElse(ItemStack.EMPTY), blocks.storageCrafting16384K().maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
    }

    /*
     * 没人知道这玩意会不会出问题，起码我不能
     * 我受够了
     */
    @Optional.Method(modid = "extracpus")
    private static void registerEXCPU() {
        if (Loader.isModLoaded("extracells")) {
            Item item = Item.getByNameOrId("extracells:storage.component");
            int i = 0;
            addMatch(new ItemStack(item, 1, i++), ModBlocks.CRAFTING_STORAGE_256K.maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
            addMatch(new ItemStack(item, 1, i++), ModBlocks.CRAFTING_STORAGE_1024K.maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
            addMatch(new ItemStack(item, 1, i++), ModBlocks.CRAFTING_STORAGE_4096K.maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
            addMatch(new ItemStack(item, 1, i++), Block.getBlockFromName("extracpus:crafting_storage_16384k"));
        } else if (Loader.isModLoaded("aeadditions")) {
            Item item = Item.getByNameOrId("aeadditions:storage.component");
            int i = 0;
            addMatch(new ItemStack(item, 1, i++), ModBlocks.CRAFTING_STORAGE_256K.maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
            addMatch(new ItemStack(item, 1, i++), ModBlocks.CRAFTING_STORAGE_1024K.maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
            addMatch(new ItemStack(item, 1, i++), ModBlocks.CRAFTING_STORAGE_4096K.maybeBlock().orElseThrow(() -> new NoSuchElementException("Block not registered")));
            addMatch(new ItemStack(item, 1, i++), Block.getBlockFromName("extracpus:crafting_storage_16384k"));
        }
    }
}