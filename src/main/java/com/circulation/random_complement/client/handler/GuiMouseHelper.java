package com.circulation.random_complement.client.handler;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiMouseHelper {

    public static final GuiMouseHelper INSTANCE = new GuiMouseHelper();
    private static final Minecraft mc = Minecraft.getMinecraft();
    @Getter
    private static ScaledResolution resolution;

    private GuiMouseHelper() {
    }

    public static int getMouseX() {
        int i = resolution.getScaledWidth();
        return Mouse.getX() * i / mc.displayWidth;
    }

    public static int getMouseY() {
        int j = resolution.getScaledHeight();
        return j - Mouse.getY() * j / mc.displayHeight - 1;
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        resolution = new ScaledResolution(mc);
    }

}
