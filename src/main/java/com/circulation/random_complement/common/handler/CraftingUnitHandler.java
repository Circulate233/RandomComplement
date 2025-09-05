package com.circulation.random_complement.common.handler;

import appeng.api.AEApi;
import appeng.block.crafting.BlockCraftingUnit;
import co.neeve.nae2.NAE2;
import com.circulation.random_complement.common.util.SimpleItem;
import dev.rlnt.extracpus.setup.ModBlocks;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CraftingUnitHandler {

    private static final Object2ObjectMap<SimpleItem, BlockCraftingUnit> CraftingUnitItemMap = new Object2ObjectOpenHashMap<>();

    private static final Object2ObjectMap<Block, ItemStack> CraftingUnitBlockMap = new Object2ObjectOpenHashMap<>();

    private static final Object2ObjectMap<Block, BlockCraftingUnit> CraftingUnitBaseMap = new Object2ObjectOpenHashMap<>();

    private static final ObjectSet<Block> CraftingUnitBaseSet = new ObjectOpenHashSet<>();

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
        addMatch(materials.cell1kPart().maybeStack(1).get(), blocks.craftingStorage1k().maybeBlock().get());
        addMatch(materials.cell4kPart().maybeStack(1).get(), blocks.craftingStorage4k().maybeBlock().get());
        addMatch(materials.cell16kPart().maybeStack(1).get(), blocks.craftingStorage16k().maybeBlock().get());
        addMatch(materials.cell64kPart().maybeStack(1).get(), blocks.craftingStorage64k().maybeBlock().get());
        addMatch(materials.engProcessor().maybeStack(1).get(), blocks.craftingAccelerator().maybeBlock().get());
    }

    @Optional.Method(modid = "nae2")
    private static void registerNAE2() {
        var def = NAE2.definitions();
        var materials = def.materials();
        var blocks = def.blocks();
        addMatch(materials.cellPart256K().maybeStack(1).get(), blocks.storageCrafting256K().maybeBlock().get());
        addMatch(materials.cellPart1024K().maybeStack(1).get(), blocks.storageCrafting1024K().maybeBlock().get());
        addMatch(materials.cellPart4096K().maybeStack(1).get(), blocks.storageCrafting4096K().maybeBlock().get());
        addMatch(materials.cellPart16384K().maybeStack(1).get(), blocks.storageCrafting16384K().maybeBlock().get());
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
            addMatch(new ItemStack(item, 1, i++), ModBlocks.CRAFTING_STORAGE_256K.maybeBlock().get());
            addMatch(new ItemStack(item, 1, i++), ModBlocks.CRAFTING_STORAGE_1024K.maybeBlock().get());
            addMatch(new ItemStack(item, 1, i++), ModBlocks.CRAFTING_STORAGE_4096K.maybeBlock().get());
            addMatch(new ItemStack(item, 1, i++), Block.getBlockFromName("extracpus:crafting_storage_16384k"));
        } else if (Loader.isModLoaded("aeadditions")) {
            Item item = Item.getByNameOrId("aeadditions:storage.component");
            int i = 0;
            addMatch(new ItemStack(item, 1, i++), ModBlocks.CRAFTING_STORAGE_256K.maybeBlock().get());
            addMatch(new ItemStack(item, 1, i++), ModBlocks.CRAFTING_STORAGE_1024K.maybeBlock().get());
            addMatch(new ItemStack(item, 1, i++), ModBlocks.CRAFTING_STORAGE_4096K.maybeBlock().get());
            addMatch(new ItemStack(item, 1, i++), Block.getBlockFromName("extracpus:crafting_storage_16384k"));
        }
    }
}