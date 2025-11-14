package com.circulation.random_complement.client;

import com._0xc4de.ae2exttable.items.ItemRegistry;
import com.blakebr0.extendedcrafting.compat.jei.tablecrafting.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.tablecrafting.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.tablecrafting.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.tablecrafting.UltimateTableCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.NotNull;

@JEIPlugin
public class JEIRecipeCatalyst implements IModPlugin {

    public static IModRegistry registration;

    public static ItemStack getOtherModsItemStack(String modId, String itemName) {
        Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modId, itemName));
        if (item != null) {
            return new ItemStack(item, 1);
        }
        return ItemStack.EMPTY;
    }

    /*
     * 唉，硬编码
     */
    @Override
    public void register(final @NotNull IModRegistry registry) {
        JEIRecipeCatalyst.registration = registry;
        final var exc = "packagedexcrafting";
        if (Loader.isModLoaded(exc)) {
            addRecipeCatalyst(exc, "combination_crafter", "extendedcrafting:combination_crafting");
            addRecipeCatalyst(exc, "marked_pedestal", "extendedcrafting:combination_crafting");
            addRecipeCatalyst(exc, "ender_crafter", "extendedcrafting:ender_crafting");
            addRecipeCatalyst(exc, "advanced_crafter", "extendedcrafting:table_crafting_5x5");
            addRecipeCatalyst(exc, "elite_crafter", "extendedcrafting:table_crafting_7x7");
            addRecipeCatalyst(exc, "ultimate_crafter", "extendedcrafting:table_crafting_9x9");
        }
        if (Loader.isModLoaded("packagedavaritia")) {
            addRecipeCatalyst("packagedavaritia", "extreme_crafter", "Avatitia.Extreme");
        }
        final var a = "packagedastral";
        if (Loader.isModLoaded(a)) {
            addRecipeCatalyst(a, "trait_crafter", "astralsorcery.altar.trait");
            addRecipeCatalyst(a, "constellation_crafter", "astralsorcery.altar.constellation");
            addRecipeCatalyst(a, "attunement_crafter", "astralsorcery.altar.attunement");
            addRecipeCatalyst(a, "discovery_crafter", "astralsorcery.altar.discovery");
        }
        if (Loader.isModLoaded("packageddraconic"))
            addRecipeCatalyst("packageddraconic", "fusion_crafter", "DraconicEvolution.Fusion");
        if (Loader.isModLoaded("ae2exttable")) addExtended();
    }

    @Optional.Method(modid = "ae2exttable")
    public void addExtended() {
        addRecipeCatalyst(ItemRegistry.BASIC_TERMINAL.getDefaultInstance(), VanillaRecipeCategoryUid.CRAFTING);
        addRecipeCatalyst(ItemRegistry.BASIC_TERMINAL.getDefaultInstance(), BasicTableCategory.UID);
        addRecipeCatalyst(ItemRegistry.WIRELESS_BASIC_TERMINAL.getDefaultInstance(), VanillaRecipeCategoryUid.CRAFTING);
        addRecipeCatalyst(ItemRegistry.WIRELESS_BASIC_TERMINAL.getDefaultInstance(), BasicTableCategory.UID);

        addRecipeCatalyst(ItemRegistry.ADVANCED_TERMINAL.getDefaultInstance(), AdvancedTableCategory.UID);
        addRecipeCatalyst(ItemRegistry.WIRELESS_ADVANCED_TERMINAL.getDefaultInstance(), AdvancedTableCategory.UID);

        addRecipeCatalyst(ItemRegistry.ELITE_TERMINAL.getDefaultInstance(), EliteTableCategory.UID);
        addRecipeCatalyst(ItemRegistry.WIRELESS_ELITE_TERMINAL.getDefaultInstance(), EliteTableCategory.UID);

        addRecipeCatalyst(ItemRegistry.ULTIMATE_TERMINAL.getDefaultInstance(), UltimateTableCategory.UID);
        addRecipeCatalyst(ItemRegistry.WIRELESS_ULTIMATE_TERMINAL.getDefaultInstance(), UltimateTableCategory.UID);
    }

    private void addRecipeCatalyst(String modid, String id, String... jei) {
        addRecipeCatalyst(getOtherModsItemStack(modid, id), jei);
    }

    private void addRecipeCatalyst(ItemStack stack, String... jei) {
        registration.addRecipeCatalyst(stack, jei);
    }

}