package com.circulation.random_complement.mixin.ae2.gui;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.MathExpressionParser;
import appeng.client.gui.implementations.GuiCraftAmount;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketCraftRequest;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.handler.RCInputHandler;
import com.circulation.random_complement.common.network.ContainerRollBACK;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiCraftAmount.class, remap = false, priority = 999)
public abstract class MixinGuiCraftAmount extends AEBaseGui {

    @Shadow
    private GuiTabButton originalGuiBtn;

    @Shadow
    private GuiButton next;

    @Shadow
    private GuiTextField amountToCraft;

    public MixinGuiCraftAmount(Container container) {
        super(container);
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true, remap = true)
    public void onActionPerformed(GuiButton btn, CallbackInfo ci) {
        if (RCInputHandler.getOldGui() == null) return;
        if (btn == this.originalGuiBtn || btn == this.next) {
            try {
                if (btn == this.originalGuiBtn) {
                    GuiScreen oldGui;
                    if ((oldGui = RCInputHandler.getOldGui()) != null) {
                        RCInputHandler.setDelayMethod(() -> Minecraft.getMinecraft().displayGuiScreen(oldGui));
                        RandomComplement.NET_CHANNEL.sendToServer(new ContainerRollBACK());
                        ci.cancel();
                    }
                } else if (btn == this.next) {
                    double resultD = MathExpressionParser.parse(this.amountToCraft.getText());
                    int result;
                    if (!(resultD <= (double) 0.0F) && !Double.isNaN(resultD)) {
                        result = (int) MathExpressionParser.round(resultD, 0);
                    } else {
                        result = 1;
                    }

                    boolean isShift = isShiftKeyDown();

                    if (isShift) {
                        GuiScreen oldGui;
                        if ((oldGui = RCInputHandler.getOldGui()) != null) {
                            RCInputHandler.setDelayMethod(() -> Minecraft.getMinecraft().displayGuiScreen(oldGui));
                        }
                    }

                    NetworkHandler.instance().sendToServer(new PacketCraftRequest(result, isShift));
                    ci.cancel();
                }
            } catch (NumberFormatException var5) {
                this.amountToCraft.setText("1");
            }
        }
    }
}
