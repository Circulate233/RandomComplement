package com.circulation.random_complement.client;

import appeng.client.gui.AEBaseGui;
import appeng.container.slot.SlotFake;
import com.circulation.random_complement.client.handler.ItemTooltipHandler;
import com.circulation.random_complement.mixin.util.FCClassUtil;
import com.glodblock.github.client.GuiUltimateEncoder;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RegItemTooltip {

    public static void regAll() {
        if (Loader.isModLoaded("ae2fc")) regAE2FCTooltips();
    }

    @Optional.Method(modid = "ae2fc")
    private static void regAE2FCTooltips() {
        ItemTooltipAdd t = () -> {
            if (((AEBaseGui) Minecraft.getMinecraft().currentScreen).getSlotUnderMouse() instanceof SlotFake) {
                return ObjectLists.singleton(I18n.format("key.ae2fc.pattern.tooltip.0", GameSettings.getKeyDisplayString(-98)));
            }
            return ObjectLists.emptyList();
        };

        ItemTooltipHandler.regItemTooltip(FCClassUtil.extendedFluidPatternTerminal, t);
        ItemTooltipHandler.regItemTooltip(FCClassUtil.fluidPatternTerminal, t);
        ItemTooltipHandler.regItemTooltip(FCClassUtil.wirelessFluidPatternTerminal, t);


        ItemTooltipHandler.regItemTooltip(GuiUltimateEncoder.class, t);
    }
}