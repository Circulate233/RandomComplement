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
        blocks.craftingStorage1k().maybeBlock()
                .ifPresent(b -> addMatch(materials.cell1kPart().maybeStack(1).orElse(ItemStack.EMPTY), b));
        blocks.craftingStorage4k().maybeBlock()
                .ifPresent(b -> addMatch(materials.cell4kPart().maybeStack(1).orElse(ItemStack.EMPTY), b));
        blocks.craftingStorage16k().maybeBlock()
                .ifPresent(b -> addMatch(materials.cell16kPart().maybeStack(1).orElse(ItemStack.EMPTY), b));
        blocks.craftingStorage64k().maybeBlock()
                .ifPresent(b -> addMatch(materials.cell64kPart().maybeStack(1).orElse(ItemStack.EMPTY), b));
        blocks.craftingAccelerator().maybeBlock()
                .ifPresent(b -> addMatch(materials.engProcessor().maybeStack(1).orElse(ItemStack.EMPTY), b));
    }

    @Optional.Method(modid = "nae2")
    private static void registerNAE2() {
        var def = NAE2.definitions();
        var materials = def.materials();
        var blocks = def.blocks();
        blocks.storageCrafting256K().maybeBlock()
                .ifPresent(b -> addMatch(materials.cellPart256K().maybeStack(1).orElse(ItemStack.EMPTY), b));
        blocks.storageCrafting1024K().maybeBlock()
                .ifPresent(b -> addMatch(materials.cellPart1024K().maybeStack(1).orElse(ItemStack.EMPTY), b));
        blocks.storageCrafting4096K().maybeBlock()
                .ifPresent(b -> addMatch(materials.cellPart4096K().maybeStack(1).orElse(ItemStack.EMPTY), b));
        blocks.storageCrafting16384K().maybeBlock()
                .ifPresent(b -> addMatch(materials.cellPart16384K().maybeStack(1).orElse(ItemStack.EMPTY), b));
    }

    /*
     * 没人知道这玩意会不会出问题，起码我不能
     * 我受够了
     */
    @Optional.Method(modid = "extracpus")
    private static void registerEXCPU() {
        final var block16384k = Block.getBlockFromName("extracpus:crafting_storage_16384k");
        final Item item;
        if (Loader.isModLoaded("extracells")) {
            item = Item.getByNameOrId("extracells:storage.component");
        } else if (Loader.isModLoaded("aeadditions")) {
            item = Item.getByNameOrId("aeadditions:storage.component");
        } else {
            item = null;
        }
        if (item != null) {
            ModBlocks.CRAFTING_STORAGE_256K.maybeBlock()
                    .ifPresent(b -> addMatch(new ItemStack(item, 1, 0), b));
            ModBlocks.CRAFTING_STORAGE_1024K.maybeBlock()
                    .ifPresent(b -> addMatch(new ItemStack(item, 1, 1), b));
            ModBlocks.CRAFTING_STORAGE_4096K.maybeBlock()
                    .ifPresent(b -> addMatch(new ItemStack(item, 1, 2), b));
            addMatch(new ItemStack(item, 1, 3), block16384k);
        }
    }
}