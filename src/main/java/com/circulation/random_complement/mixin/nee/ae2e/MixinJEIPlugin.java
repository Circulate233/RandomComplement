package com.circulation.random_complement.mixin.nee.ae2e;

import com._0xc4de.ae2exttable.client.container.terminals.ContainerAdvancedCraftingTerminal;
import com._0xc4de.ae2exttable.client.container.terminals.ContainerBasicCraftingTerminal;
import com._0xc4de.ae2exttable.client.container.terminals.ContainerEliteCraftingTerminal;
import com._0xc4de.ae2exttable.client.container.terminals.ContainerUltimateCraftingTerminal;
import com._0xc4de.ae2exttable.client.container.wireless.ContainerAdvancedWirelessTerminal;
import com._0xc4de.ae2exttable.client.container.wireless.ContainerBasicWirelessTerminal;
import com._0xc4de.ae2exttable.client.container.wireless.ContainerEliteWirelessTerminal;
import com._0xc4de.ae2exttable.client.container.wireless.ContainerUltimateWirelessTerminal;
import com._0xc4de.ae2exttable.integration.JEIPlugin;
import com.github.vfyjxf.nee.jei.CraftingTransferHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = JEIPlugin.class,remap = false)
public class MixinJEIPlugin implements IModPlugin {

    /**
     * @author Circulation
     * @reason 修改配方传输器为NEE注册的
     */
    @Overwrite
    public void register(IModRegistry registry) {
        IRecipeTransferRegistry transfer = registry.getRecipeTransferRegistry();
        transfer.addRecipeTransferHandler(new CraftingTransferHandler<>(ContainerBasicCraftingTerminal.class), VanillaRecipeCategoryUid.CRAFTING);
        transfer.addRecipeTransferHandler(new CraftingTransferHandler<>(ContainerBasicCraftingTerminal.class), "extendedcrafting:table_crafting_3x3");
        transfer.addRecipeTransferHandler(new CraftingTransferHandler<>(ContainerBasicWirelessTerminal.class), VanillaRecipeCategoryUid.CRAFTING);
        transfer.addRecipeTransferHandler(new CraftingTransferHandler<>(ContainerBasicWirelessTerminal.class), "extendedcrafting:table_crafting_3x3");
        transfer.addRecipeTransferHandler(new CraftingTransferHandler<>(ContainerAdvancedCraftingTerminal.class), "extendedcrafting:table_crafting_5x5");
        transfer.addRecipeTransferHandler(new CraftingTransferHandler<>(ContainerAdvancedWirelessTerminal.class), "extendedcrafting:table_crafting_5x5");
        transfer.addRecipeTransferHandler(new CraftingTransferHandler<>(ContainerEliteCraftingTerminal.class), "extendedcrafting:table_crafting_7x7");
        transfer.addRecipeTransferHandler(new CraftingTransferHandler<>(ContainerEliteWirelessTerminal.class), "extendedcrafting:table_crafting_7x7");
        transfer.addRecipeTransferHandler(new CraftingTransferHandler<>(ContainerUltimateCraftingTerminal.class), "extendedcrafting:table_crafting_9x9");
        transfer.addRecipeTransferHandler(new CraftingTransferHandler<>(ContainerUltimateWirelessTerminal.class), "extendedcrafting:table_crafting_9x9");
    }
}
