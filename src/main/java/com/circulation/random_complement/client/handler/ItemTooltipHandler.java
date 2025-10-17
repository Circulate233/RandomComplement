package com.circulation.random_complement.client.handler;

import com.circulation.random_complement.client.ItemTooltipAdd;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ItemTooltipHandler {

    private static final Map<Class<? extends GuiScreen>, List<ItemTooltipAdd>> map = new Reference2ObjectLinkedOpenHashMap<>();

    public static void regItemTooltip(Class<? extends GuiScreen> aClass, ItemTooltipAdd tooltip) {
        synchronized (map) {
            map.computeIfAbsent(aClass, c -> new ObjectArrayList<>())
                    .add(tooltip);
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        final var gui = Minecraft.getMinecraft().currentScreen;
        if (gui == null) return;
        if (GuiScreen.isAltKeyDown()) {
            for (var entry : map.entrySet()) {
                if (entry.getKey().isInstance(gui)) {
                    for (var tooltip : entry.getValue()) {
                        event.getToolTip().addAll(tooltip.get());
                    }
                }
            }
        } else {
            boolean b = false;
            for (var entry : map.entrySet()) {
                if (entry.getKey().isInstance(gui)) {
                    for (var tooltip : entry.getValue()) {
                        if (tooltip.get().isEmpty()) continue;
                        b = true;
                        break;
                    }
                }
            }
            if (b) event.getToolTip().add(I18n.format("hei.tooltip.press_alt"));
        }
    }

}