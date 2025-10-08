package com.circulation.random_complement.client.handler;

import com.circulation.random_complement.common.util.XYPair;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class RCJEIInputHandler {

    public static RCJEIInputHandler INSTANCE = new RCJEIInputHandler();

    private static final ReferenceSet<Class<?>> jeiGui = new ReferenceOpenHashSet<>();

    public static int getJeiGuiSize() {
        return jeiGui.size();
    }

    public static void addJeiGui(Class<?> c) {
        jeiGui.add(c);
    }

    public static boolean isJeiGui(GuiScreen gui) {
        for (var aClass : jeiGui) {
            if (aClass.isInstance(gui)) {
                return true;
            }
        }
        return false;
    }

    @Setter
    @Getter
    private static boolean click;
    @Setter
    private static Runnable clickCache;
    @Getter
    private static XYPair xy;

    public static void runClickCache() {
        if (clickCache != null) {
            clickCache.run();
        }
        clearCache();
    }

    public static void clearCache() {
        click = false;
        clickCache = null;
        xy = null;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClickEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (isJeiGui(Minecraft.getMinecraft().currentScreen)) {
            int eventButton = Mouse.getEventButton();
            if (eventButton > -1) {
                if (Mouse.isButtonDown(eventButton)) {
                    click = true;
                    xy = XYPair.of(Mouse.getX(), Mouse.getY());
                } else if (clickCache != null) {
                    runClickCache();
                }
            }
        }
    }

    private RCJEIInputHandler() {

    }

}