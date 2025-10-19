package com.circulation.random_complement.client.handler;

import com.circulation.random_complement.common.util.XYPair;
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

    @Setter
    private static Class<?>[] jeiGui;
    @Setter
    @Getter
    private static boolean click;
    @Setter
    private static Runnable clickCache;
    @Setter
    private static Runnable shiftClickCache;
    @Getter
    private static XYPair xy;

    private RCJEIInputHandler() {

    }

    public static boolean isJeiGui(GuiScreen gui) {
        for (var aClass : jeiGui) {
            if (aClass.isInstance(gui)) {
                return true;
            }
        }
        return false;
    }

    public static void runClickCache() {
        if (clickCache != null) {
            if (shiftClickCache == null || !GuiScreen.isShiftKeyDown()) {
                clickCache.run();
            } else {
                shiftClickCache.run();
            }
        }
        clearCache();
    }

    public static void clearCache() {
        click = false;
        clickCache = null;
        shiftClickCache = null;
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

}