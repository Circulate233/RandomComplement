package com.circulation.random_complement.client;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiScrollbar;
import com.circulation.random_complement.client.handler.RCInputHandler;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

import java.awt.Rectangle;

public class RCGuiScrollbar extends GuiScrollbar {

    @Getter
    private final Rectangle rectangle = new Rectangle(getLeft(), getTop(), getWidth(), getHeight());
    @Getter
    private boolean visible = true;

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            rcScrollbarGui.onCurrentScrollChance(getCurrentScroll());
        }
    }

    @Setter
    private RCScrollbarGui rcScrollbarGui;

    @Setter
    private int leftEx = 0;
    @Setter
    private int widthEx = 0;

    @Setter
    private Runnable drawBG;

    public void drawBG() {
        if (drawBG != null) {
            drawBG.run();
        }
    }

    @Override
    public void draw(AEBaseGui gui) {
        if (visible) {
            super.draw(gui);
        }
    }

    @Override
    public GuiScrollbar setLeft(int v) {
        rectangle.x = v - leftEx;
        return super.setLeft(v);
    }

    @Override
    public GuiScrollbar setTop(int v) {
        rectangle.y = v;
        return super.setTop(v);
    }

    @Override
    public GuiScrollbar setWidth(int v) {
        rectangle.width = v + widthEx;
        return super.setWidth(v);
    }

    @Override
    public GuiScrollbar setHeight(int v) {
        rectangle.height = v;
        return super.setHeight(v);
    }

    public void click(AEBaseGui aeBaseGui, int x, int y) {
        if (visible) {
            int old = getCurrentScroll();
            super.click(aeBaseGui, x, y);
            if (old != getCurrentScroll() && rcScrollbarGui != null) {
                rcScrollbarGui.onCurrentScrollChance(getCurrentScroll());
            }
        }
    }

    public void wheel(int delta) {
        if (visible) {
            if (rectangle.contains(RCInputHandler.getMouseX() - ((AEBaseGui) Minecraft.getMinecraft().currentScreen).getGuiLeft(), RCInputHandler.getMouseY() - ((AEBaseGui) Minecraft.getMinecraft().currentScreen).getGuiTop())) {
                int old = getCurrentScroll();
                super.wheel(delta);
                if (old != getCurrentScroll() && rcScrollbarGui != null) {
                    rcScrollbarGui.onCurrentScrollChance(getCurrentScroll());
                }
            }
        }
    }
}