package com.circulation.random_complement.mixin.ae2.gui;

import appeng.client.gui.implementations.GuiCraftingCPU;
import appeng.client.gui.implementations.GuiCraftingStatus;
import appeng.container.implementations.CraftingCPUStatus;
import com.circulation.random_complement.common.integration.ae2.core.GuiColors;
import com.llamalad7.mixinextras.sugar.Local;
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

    /**
     * @author sddsd2332
     * @reason 在制作 cpu 表中增加进度表
     *
     * <a href="https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/pull/698">代码来自GTNH团队的AE2U。</a>
     */
    @Redirect(method = "drawFG", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal = 2))
    private void draw(@Local(name = "cpu") CraftingCPUStatus cpuStatus) {
        if (cpuStatus != null) {
            GL11.glPushMatrix();
            double craftingPercentage = (double) (cpuStatus.getTotalItems() - Math.max(cpuStatus.getRemainingItems(), 0)) / (double) cpuStatus.getTotalItems();
            drawRect(-85, 20, -85 + (int) (66 * craftingPercentage), 21, this.randomComplement$calculateGradientColor(craftingPercentage));
        }
    }

}
