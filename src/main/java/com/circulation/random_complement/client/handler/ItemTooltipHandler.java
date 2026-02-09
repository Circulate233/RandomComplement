package com.circulation.random_complement.client.handler;

import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.ItemTooltipAdd;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ItemTooltipHandler {

    public static final ItemTooltipHandler INSTANCE = new ItemTooltipHandler();
    private static final Map<Class<? extends GuiScreen>, List<ItemTooltipAdd>> map = new Reference2ObjectLinkedOpenHashMap<>();

    public static void regItemTooltip(Class<? extends GuiScreen> aClass, ItemTooltipAdd tooltip) {
        synchronized (map) {
            map.computeIfAbsent(aClass, c -> new ObjectArrayList<>())
               .add(tooltip);
        }
    }

    private ItemTooltipHandler() {
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
            for (var entry : map.entrySet()) {
                if (entry.getKey().isInstance(gui)) {
                    for (var tooltip : entry.getValue()) {
                        if (tooltip.get().isEmpty()) continue;
                        event.getToolTip().add(I18n.format("text.rc.tooltip.press_alt"));
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTooltip(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (RandomComplement.proxy.isMouseHasItem()) return;
        final var gui = Minecraft.getMinecraft().currentScreen;
        if (gui == null) return;
        if (GuiScreen.isAltKeyDown()) {
            for (var entry : map.entrySet()) {
                if (entry.getKey().isInstance(gui)) {
                    for (var tooltip : entry.getValue()) {
                        gui.drawHoveringText(tooltip.get(), GuiMouseHelper.getMouseX(), GuiMouseHelper.getMouseY());
                    }
                }
            }
        } else {
            for (var entry : map.entrySet()) {
                if (entry.getKey().isInstance(gui)) {
                    for (var tooltip : entry.getValue()) {
                        if (tooltip.get().isEmpty()) continue;
                        gui.drawHoveringText(Collections.singletonList(I18n.format("text.rc.tooltip.press_alt")), GuiMouseHelper.getMouseX(), GuiMouseHelper.getMouseY());
                        break;
                    }
                }
            }
        }
    }

}