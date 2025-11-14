package com.circulation.random_complement.mixin.ae2.gui;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiCraftingCPU;
import appeng.client.gui.widgets.ISortSource;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerCraftingCPU;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketInventoryAction;
import appeng.helpers.InventoryAction;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.common.interfaces.getCraftingCPUCluster;
import com.google.common.base.Joiner;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Mixin(GuiCraftingCPU.class)
public abstract class MixinGuiCraftingCPU extends AEBaseGui implements ISortSource {

    @Shadow(remap = false)
    @Final
    private ContainerCraftingCPU craftingCpu;
    @Shadow(remap = false)
    private List<IAEItemStack> visual;
    @Unique
    private IAEItemStack randomComplement$hoveredAEStack;

    public MixinGuiCraftingCPU(Container container) {
        super(container);
    }

    @Unique
    private static String randomComplement$getItemInformation(final Object o) {
        String dspToolTip = "";
        List<String> lineList = new ObjectArrayList<>();
        if (o == null) {
            return "** Null";
        }
        ITooltipFlag.TooltipFlags tooltipFlag = ITooltipFlag.TooltipFlags.NORMAL;
        try {
            tooltipFlag = Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
        } catch (final Exception ignored) {

        }
        ItemStack itemStack = ItemStack.EMPTY;
        if (o instanceof AEItemStack aeItemStack) {
            try {
                aeItemStack.getItem().addInformation(aeItemStack.getDefinition(), null, lineList, tooltipFlag);
            } catch (Exception ignored) {
            }
        } else if (o instanceof ItemStack stack) {
            itemStack = stack;
        } else return "";
        try {
            itemStack.getItem().addInformation(itemStack, null, lineList, tooltipFlag);
        } catch (Exception ignored) {
        }

        if (!lineList.isEmpty()) {
            dspToolTip = dspToolTip + '\n' + Joiner.on("\n").join(lineList);
        }
        return dspToolTip;
    }

    /**
     * @author sddsd2332
     * @reason 额外添加制作耗时
     */
    @WrapOperation(method = "drawFG", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", ordinal = 0), remap = false)
    public int getCumulativeTime(FontRenderer instance, String text, int x, int y, int color, Operation<Integer> original) {
        if (craftingCpu instanceof getCraftingCPUCluster craftingCPU) {
            long getTime = craftingCPU.randomComplement$elapsedTime();
            if (getTime > 0L && !this.visual.isEmpty()) {
                long elapsedTime = TimeUnit.MILLISECONDS.convert(getTime, TimeUnit.NANOSECONDS);
                String etaTimeText = DurationFormatUtils.formatDuration(elapsedTime, GuiText.ETAFormat.getLocal());
                text = text + " " + I18n.format("gui.appliedenergistics2.CraftingStatusCumulativeTime") + " - " + etaTimeText;
            }
        }
        return original.call(instance, text, x, y, color);
    }

    /**
     * @author sddsd2332
     * @reason 实现物品在合成时，能够再次下单该物品
     *
     * <a href="https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/pull/704">代码来自GTNH团队的AE2U。</a>
     */
    @Inject(method = "drawFG", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1), remap = false)
    public void getHoveredAEStack(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci, @Local(name = "refStack") IAEItemStack stack) {
        randomComplement$hoveredAEStack = stack;
    }

    @WrapOperation(method = "drawFG", at = @At(value = "INVOKE", target = "Lappeng/util/Platform;getItemDisplayName(Ljava/lang/Object;)Ljava/lang/String;"), remap = false)
    public String addItemInformation(Object n, Operation<String> original) {
        String out;
        if (!(out = randomComplement$getItemInformation(n)).isEmpty()) {
            return original.call(n) + out;
        } else {
            return original.call(n);
        }
    }

    @Intrinsic
    protected void mouseClicked(final int xCord, final int yCord, final int btn) throws IOException {
        if (randomComplement$hoveredAEStack != null && btn == 2) {
            ((AEBaseContainer) inventorySlots).setTargetStack(randomComplement$hoveredAEStack);
            final PacketInventoryAction p = new PacketInventoryAction(
                    InventoryAction.AUTO_CRAFT,
                    inventorySlots.inventorySlots.size(),
                    0);
            NetworkHandler.instance.sendToServer(p);
        }
        super.mouseClicked(xCord, yCord, btn);
        //   this.searchField.mouseClicked(xCord, yCord, btn);
    }

}