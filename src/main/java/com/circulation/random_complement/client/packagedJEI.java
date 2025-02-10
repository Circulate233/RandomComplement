package com.circulation.random_complement.client;

import mezz.jei.api.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.NotNull;

@JEIPlugin
public class packagedJEI implements IModPlugin {

    public static IModRegistry registration;

    /*
     * 唉，硬编码
     */
    @Override
    public void register(final @NotNull IModRegistry registry) {
        packagedJEI.registration = registry;
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
    }

    public static ItemStack getOtherModsItemStack(String modId, String itemName) {
        Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modId, itemName));
        if (item != null) {
            return new ItemStack(item,1);
        }
        return ItemStack.EMPTY;
    }

}
