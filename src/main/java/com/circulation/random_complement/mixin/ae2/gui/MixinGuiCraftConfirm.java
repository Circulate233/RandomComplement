package com.circulation.random_complement.mixin.ae2.gui;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiCraftConfirm;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.google.common.base.Joiner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(GuiCraftConfirm.class)
public abstract class MixinGuiCraftConfirm extends AEBaseGui {
    public MixinGuiCraftConfirm(Container container) {
        super(container);
    }

    @Redirect(method = "drawFG", at = @At(value = "INVOKE", target = "Lappeng/util/Platform;getItemDisplayName(Ljava/lang/Object;)Ljava/lang/String;"), remap = false)
    public String addItemInformation(Object n) {
        String out;
        if (!(out = randomComplement$getItemInformation(n)).isEmpty()) {
            return randomComplement$getItemDisplayName(n) + out;
        } else {
            return randomComplement$getItemDisplayName(n);
        }
    }

    @Unique
    private static String randomComplement$getItemInformation(final Object o) {
        String dspToolTip = "";
        List<String> lineList = new ArrayList<>();
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
        } else {
            return "";
        }

        try {
            itemStack.getItem().addInformation(itemStack, null, lineList, tooltipFlag);
        } catch (Exception ignored) {
        }

        if (!lineList.isEmpty()) {
            dspToolTip = dspToolTip + '\n' + Joiner.on("\n").join(lineList);
        }
        return dspToolTip;
    }


    @Unique
    private String randomComplement$getItemDisplayName(Object n) {
        return Platform.getItemDisplayName(n);
    }
}
