package com.circulation.random_complement.client.handler;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;

public class SlotMEHandler {

    public static void drawPlus(int x, int y) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float startX = x + 0.5f;
        float startY = y + 0.25f;
        float endX = startX + 3f;
        float endY = startY + 3f;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(0, 0, 250);

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        buffer.pos(startX, startY + 1.5f, 0).endVertex();
        buffer.pos(endX, startY + 1.5f, 0).endVertex();
        tessellator.draw();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        buffer.pos(startX + 1.5f, startY, 0).endVertex();
        buffer.pos(startX + 1.5f, endY, 0).endVertex();
        tessellator.draw();

        GlStateManager.translate(0, 0, -250);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawPlus(Slot slot) {
        int x = slot.xPos;
        int y = slot.yPos;

        drawPlus(x,y);
    }
}
