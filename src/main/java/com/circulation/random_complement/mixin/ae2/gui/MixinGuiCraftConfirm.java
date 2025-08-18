package com.circulation.random_complement.mixin.ae2.gui;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiCraftConfirm;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketValueConfig;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.handler.InputHandler;
import com.circulation.random_complement.common.network.ContainerRollBACK;
import com.google.common.base.Joiner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = GuiCraftConfirm.class,priority = 999)
public abstract class MixinGuiCraftConfirm extends AEBaseGui {
    public MixinGuiCraftConfirm(Container container) {
        super(container);
    }

    @Shadow(remap = false)
    private GuiButton cancel;

    @Shadow(remap = false)
    private GuiButton start;

    @Inject(
            method = "actionPerformed",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/client/gui/AEBaseGui;actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V",
                    shift = At.Shift.AFTER
            ), cancellable = true,remap = false)
    public void onActionPerformed1(GuiButton btn, CallbackInfo ci) {
        if (InputHandler.oldGui == null)return;
        if (btn == this.start || btn == cancel) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (btn == this.start) {
                    try {
                        NetworkHandler.instance().sendToServer(new PacketValueConfig("Terminal.Start", "Start"));
                    } catch (Throwable e) {
                        AELog.debug(e);
                    }
                }

                GuiScreen oldGui;
                if ((oldGui = InputHandler.oldGui) != null) {
                    InputHandler.delayMethod = () -> Minecraft.getMinecraft().displayGuiScreen(oldGui);
                    RandomComplement.NET_CHANNEL.sendToServer(new ContainerRollBACK());
                    ci.cancel();
                }
            });
            if (InputHandler.oldGui != null) {
                ci.cancel();
            }
        }
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
