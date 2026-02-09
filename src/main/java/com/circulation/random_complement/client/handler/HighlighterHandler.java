package com.circulation.random_complement.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import static appeng.helpers.HighlighterHandler.renderHighLightedBlocksOutline;

@SideOnly(Side.CLIENT)
public class HighlighterHandler {

    public static final HighlighterHandler INSTANCE = new HighlighterHandler();
    private BlockPos[] hilightedBlock;
    private long expireHilight;
    private int dimension;

    private HighlighterHandler() {
    }

    public void hilightBlock(BlockPos[] c, long expireHilight, int dimension) {
        this.hilightedBlock = c;
        this.expireHilight = expireHilight;
        this.dimension = dimension;
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        if (hilightedBlock == null) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        int dimension = mc.world.provider.getDimension();
        long time = System.currentTimeMillis();

        if (time > expireHilight || dimension != this.dimension) {
            hilightBlock(null, -1, this.dimension);
            return;
        }

        if (((time / 500) & 1) == 0) {
            return;
        }

        EntityPlayerSP p = mc.player;
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * event.getPartialTicks();
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * event.getPartialTicks();
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * event.getPartialTicks();

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 0, 0);
        GlStateManager.glLineWidth(3);
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        //noinspection ForLoopReplaceableByForEach
        for (var i = 0; i < hilightedBlock.length; i++) {
            var pos = hilightedBlock[i];
            if (pos == null) return;
            float mx = pos.getX();
            float my = pos.getY();
            float mz = pos.getZ();
            renderHighLightedBlocksOutline(buffer, mx, my, mz, 1.0f, 0.0f, 0.0f, 1.0f);
        }

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
}
