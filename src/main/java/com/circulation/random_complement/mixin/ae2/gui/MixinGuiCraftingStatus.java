package com.circulation.random_complement.mixin.ae2.gui;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.implementations.GuiCraftingCPU;
import appeng.client.gui.implementations.GuiCraftingStatus;
import appeng.container.implementations.CraftingCPUStatus;
import com.circulation.random_complement.common.integration.ae2.core.GuiColors;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiCraftingStatus.class, remap = false)
public abstract class MixinGuiCraftingStatus extends GuiCraftingCPU {

    public MixinGuiCraftingStatus(InventoryPlayer inventoryPlayer, Object te) {
        super(inventoryPlayer, te);
    }

    @Unique
    private static int randomComplement$processBarStartColorInt = GuiColors.ProcessBarStartColor.getColor();

    @Unique
    private static final int[] PROCESS_BAR_START_COLOR_INT_ARR = new int[]{(randomComplement$processBarStartColorInt >> 24) & 0xFF, (randomComplement$processBarStartColorInt >> 16) & 0xFF, (randomComplement$processBarStartColorInt >> 8) & 0xFF, randomComplement$processBarStartColorInt & 0xFF};

    @Unique
    private static int randomComplement$processBarMiddleColorInt = GuiColors.ProcessBarMiddleColor.getColor();

    @Unique
    private static final int[] PROCESS_BAR_MIDDLE_COLOR_INT_ARR = new int[]{(randomComplement$processBarMiddleColorInt >> 24) & 0xFF, (randomComplement$processBarMiddleColorInt >> 16) & 0xFF, (randomComplement$processBarMiddleColorInt >> 8) & 0xFF, randomComplement$processBarMiddleColorInt & 0xFF};

    @Unique
    private static int randomComplement$processBarEndColorInt = GuiColors.ProcessBarEndColor.getColor();

    @Unique
    private static final int[] PROCESS_BAR_END_COLOR_INT_ARR = new int[]{(randomComplement$processBarEndColorInt >> 24) & 0xFF, (randomComplement$processBarEndColorInt >> 16) & 0xFF, (randomComplement$processBarEndColorInt >> 8) & 0xFF, randomComplement$processBarEndColorInt & 0xFF};

    @Unique
    private int randomComplement$calculateGradientColor(double percentage) {
        int[] start;
        int[] end;
        double ratio;
        if (percentage <= 0.5) {
            start = PROCESS_BAR_START_COLOR_INT_ARR;
            end = PROCESS_BAR_MIDDLE_COLOR_INT_ARR;
            ratio = percentage * 2;
        } else {
            start = PROCESS_BAR_MIDDLE_COLOR_INT_ARR;
            end = PROCESS_BAR_END_COLOR_INT_ARR;
            ratio = (percentage - 0.5d) * 2;
        }
        int a = (int) (start[0] + ratio * (end[0] - start[0]));
        int r = (int) (start[1] + ratio * (end[1] - start[1]));
        int g = (int) (start[2] + ratio * (end[2] - start[2]));
        int b = (int) (start[3] + ratio * (end[3] - start[3]));
        return (a << 24) | (r << 16) | (g << 8) | (b);
    }

    @Unique
    private int randomComplement$yPos;

    @Unique
    private CraftingCPUStatus randomComplement$cpuStatus;

    @Redirect(method = "drawFG", at = @At(value = "INVOKE", target = "Lappeng/client/gui/implementations/GuiCraftingStatus;drawTexturedModalRect(IIIIII)V", ordinal = 0))
    private void setYPos(GuiCraftingStatus instance, int x, int y, int textureX, int textureY, int width, int height) {
        randomComplement$yPos = y;
        drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    @Redirect(method = "drawFG", at = @At(value = "INVOKE", target = "Lappeng/container/implementations/CraftingCPUStatus;getCrafting()Lappeng/api/storage/data/IAEItemStack;", ordinal = 0))
    private IAEItemStack getCpuStatus(CraftingCPUStatus instance) {
        randomComplement$cpuStatus = instance;
        return instance.getCrafting();
    }

    /**
     * @author sddsd2332
     * @reason 在制作 cpu 表中增加进度表
     *
     * <a href="https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/pull/698">代码来自GTNH团队的AE2U。</a>
     */
    @Redirect(method = "drawFG", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal = 2))
    private void draw() {
        GL11.glPushMatrix();
        double craftingPercentage = (double) (randomComplement$cpuStatus.getTotalItems() - Math.max(randomComplement$cpuStatus.getRemainingItems(), 0)) / (double) randomComplement$cpuStatus.getTotalItems();
        drawRect(-85, randomComplement$yPos + 23 - 3, -85 + (int) ((67 - 1) * craftingPercentage), randomComplement$yPos + 23 - 2, this.randomComplement$calculateGradientColor(craftingPercentage));
    }

}
