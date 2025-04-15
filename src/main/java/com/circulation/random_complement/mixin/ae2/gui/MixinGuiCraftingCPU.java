package com.circulation.random_complement.mixin.ae2.gui;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiCraftingCPU;
import appeng.client.gui.widgets.ISortSource;
import appeng.container.AEBaseContainer;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketInventoryAction;
import appeng.helpers.InventoryAction;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.google.common.base.Joiner;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(GuiCraftingCPU.class)
public abstract class MixinGuiCraftingCPU extends AEBaseGui implements ISortSource {

    @Unique
    private IAEItemStack randomComplement$hoveredAEStack;

    public MixinGuiCraftingCPU(Container container) {
        super(container);
    }

    /**
     * @author sddsd2332
     * @reason 实现物品在合成时，能够再次下单该物品
     *
     * <a href="https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/pull/704">代码来自GTNH团队的AE2U。</a>
     */
    @Inject(method = "drawFG", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z",ordinal = 1), remap = false)
    public void getHoveredAEStack(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci, @Local(ordinal = 0) IAEItemStack stack) {
        randomComplement$hoveredAEStack = stack;
    }


    @Redirect(method = "drawFG", at = @At(value = "INVOKE", target = "Lappeng/util/Platform;getItemDisplayName(Ljava/lang/Object;)Ljava/lang/String;"), remap = false)
    public String addItemInformation(Object n) {
        if (!randomComplement$getItemInformation(n).isEmpty()) {
            return randomComplement$getItemDisplayName(n) + randomComplement$getItemInformation(n);
        } else {
            return randomComplement$getItemDisplayName(n);
        }

    }

    @Override
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
            aeItemStack.getItem().addInformation(aeItemStack.getDefinition(), null, lineList, tooltipFlag);
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