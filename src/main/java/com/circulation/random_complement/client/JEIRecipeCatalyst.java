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
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.NotNull;

@JEIPlugin
public class JEIRecipeCatalyst implements IModPlugin {

    public static IModRegistry registration;

    /*
     * 唉，硬编码
     */
    @Override
    public void register(final @NotNull IModRegistry registry) {
        JEIRecipeCatalyst.registration = registry;
        if (Loader.isModLoaded("packagedexcrafting")) {
            registration.addRecipeCatalyst(getOtherModsItemStack("packagedexcrafting", "combination_crafter"), "extendedcrafting:combination_crafting");
            registration.addRecipeCatalyst(getOtherModsItemStack("packagedexcrafting", "marked_pedestal"), "extendedcrafting:combination_crafting");
            registration.addRecipeCatalyst(getOtherModsItemStack("packagedexcrafting", "ender_crafter"), "extendedcrafting:ender_crafting");
            registration.addRecipeCatalyst(getOtherModsItemStack("packagedexcrafting", "advanced_crafter"), "extendedcrafting:table_crafting_5x5");
            registration.addRecipeCatalyst(getOtherModsItemStack("packagedexcrafting", "elite_crafter"), "extendedcrafting:table_crafting_7x7");
            registration.addRecipeCatalyst(getOtherModsItemStack("packagedexcrafting", "ultimate_crafter"), "extendedcrafting:table_crafting_9x9");
        }
        if (Loader.isModLoaded("packagedavaritia")) {
            registration.addRecipeCatalyst(getOtherModsItemStack("packagedavaritia", "extreme_crafter"), "Avatitia.Extreme");
        }
        if (Loader.isModLoaded("packagedastral")) {
            registration.addRecipeCatalyst(getOtherModsItemStack("packagedastral", "trait_crafter"), "astralsorcery.altar.trait");
            registration.addRecipeCatalyst(getOtherModsItemStack("packagedastral", "constellation_crafter"), "astralsorcery.altar.constellation");
            registration.addRecipeCatalyst(getOtherModsItemStack("packagedastral", "attunement_crafter"), "astralsorcery.altar.attunement");
            registration.addRecipeCatalyst(getOtherModsItemStack("packagedastral", "discovery_crafter"), "astralsorcery.altar.discovery");
        }
        if (Loader.isModLoaded("packageddraconic")) {
            registration.addRecipeCatalyst(getOtherModsItemStack("packageddraconic", "fusion_crafter"), "DraconicEvolution.Fusion");
        }
        if (Loader.isModLoaded("ae2exttable")) {
            addExtended();
        }
    }

    public void addExtended(){
        registration.addRecipeCatalyst(ItemRegistry.BASIC_TERMINAL.getDefaultInstance(), VanillaRecipeCategoryUid.CRAFTING);
        registration.addRecipeCatalyst(ItemRegistry.BASIC_TERMINAL.getDefaultInstance(), BasicTableCategory.UID);
        registration.addRecipeCatalyst(ItemRegistry.WIRELESS_BASIC_TERMINAL.getDefaultInstance(),VanillaRecipeCategoryUid.CRAFTING);
        registration.addRecipeCatalyst(ItemRegistry.WIRELESS_BASIC_TERMINAL.getDefaultInstance(),BasicTableCategory.UID);

        registration.addRecipeCatalyst(ItemRegistry.ADVANCED_TERMINAL.getDefaultInstance(),AdvancedTableCategory.UID);
        registration.addRecipeCatalyst(ItemRegistry.WIRELESS_ADVANCED_TERMINAL.getDefaultInstance(),AdvancedTableCategory.UID);

        registration.addRecipeCatalyst(ItemRegistry.ELITE_TERMINAL.getDefaultInstance(),EliteTableCategory.UID);
        registration.addRecipeCatalyst(ItemRegistry.WIRELESS_ELITE_TERMINAL.getDefaultInstance(),EliteTableCategory.UID);

        registration.addRecipeCatalyst(ItemRegistry.ULTIMATE_TERMINAL.getDefaultInstance(),UltimateTableCategory.UID);
        registration.addRecipeCatalyst(ItemRegistry.WIRELESS_ULTIMATE_TERMINAL.getDefaultInstance(),UltimateTableCategory.UID);
    }

    public static ItemStack getOtherModsItemStack(String modId, String itemName) {
        Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modId, itemName));
        if (item != null) {
            return new ItemStack(item,1);
        }
        return ItemStack.EMPTY;
    }

}
