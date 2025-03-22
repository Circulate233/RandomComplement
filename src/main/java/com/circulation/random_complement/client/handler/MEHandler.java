package com.circulation.random_complement.client.handler;

import appeng.api.AEApi;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.container.slot.SlotRestrictedInput;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Unique;

public class MEHandler {

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

    /*
     * 使用了GTNH团队的AE2U的相关方法
     * https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/blob/c3cd45df5db9db256b9fb4774b2cb57fdf11e389/src/main/java/appeng/container/implementations/ContainerMEMonitorable.java#L397
     */
    @Unique
    public static void refillBlankPatterns(ContainerMEMonitorable container, SlotRestrictedInput slot) {
        if (Platform.isServer()) {
            ItemStack blanks = slot.getStack();
            int blanksToRefill = 64;
            blanksToRefill -= blanks.getCount();
            if (blanksToRefill <= 0) return;
            var blankPattern = AEApi.instance().definitions().materials().blankPattern().maybeStack(blanksToRefill);
            if (blankPattern.isPresent()) {
                final AEItemStack request = AEItemStack
                        .fromItemStack(blankPattern.get());
                final IAEItemStack extracted = Platform
                        .poweredExtraction(container.getPowerSource(), container.getCellInventory(), request, container.getActionSource());
                if (extracted != null) {
                    if (blanks.isEmpty()){
                        blanks = request.getDefinition().copy();
                        blanks.setCount((int) (extracted.getStackSize()));
                    } else {
                        blanks.setCount((int) (blanks.getCount() + extracted.getStackSize()));
                    }
                    slot.putStack(blanks);
                }
            }
        }
    }
}
