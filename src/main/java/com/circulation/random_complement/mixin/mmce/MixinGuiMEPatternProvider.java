package com.circulation.random_complement.mixin.mmce;

import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.RCGuiButton;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.Action;
import com.circulation.random_complement.common.network.RCActionButton;
import github.kasuminova.mmce.client.gui.AEBaseGuiContainerDynamic;
import github.kasuminova.mmce.client.gui.GuiMEPatternProvider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMEPatternProvider.class)
public class MixinGuiMEPatternProvider extends AEBaseGuiContainerDynamic {

    @Unique
    private RCGuiButton r$DIVIDE_2;
    @Unique
    private RCGuiButton r$DIVIDE_3;
    @Unique
    private RCGuiButton r$MULTIPLY_2;
    @Unique
    private RCGuiButton r$MULTIPLY_3;

    public MixinGuiMEPatternProvider(Container container) {
        super(container);
    }

    @Inject(method = "initGui", at = @At("TAIL"))
    public void onInitGui(CallbackInfo ci) {
        int i = 0;
        final int left = this.guiLeft - 18;
        final int top = r$getTop();
        this.r$MULTIPLY_2 = new RCGuiButton(left, top + i++ * 18, RCSettings.ACTIONS, Action.MULTIPLY_2);
        this.buttonList.add(this.r$MULTIPLY_2);
        this.r$DIVIDE_2 = new RCGuiButton(left, top + i++ * 18, RCSettings.ACTIONS, Action.DIVIDE_2);
        this.buttonList.add(this.r$DIVIDE_2);
        this.r$MULTIPLY_3 = new RCGuiButton(left, top + i++ * 18, RCSettings.ACTIONS, Action.MULTIPLY_3);
        this.buttonList.add(this.r$MULTIPLY_3);
        this.r$DIVIDE_3 = new RCGuiButton(left, top + i++ * 18, RCSettings.ACTIONS, Action.DIVIDE_3);
        this.buttonList.add(this.r$DIVIDE_3);
    }

    @Unique
    public int r$getTop() {
        int top = this.guiTop + 8;
        final int left = this.guiLeft - 18;
        for (GuiButton guiButton : this.buttonList) {
            if (guiButton.x != left) continue;
            if (top < guiButton.y) top = guiButton.y;
        }
        return top;
    }

    @Unique
    @Override
    protected void actionPerformed(@NotNull GuiButton btn) {
        boolean backwards = Mouse.isButtonDown(1);
        if (btn == this.r$DIVIDE_2) {
            RandomComplement.NET_CHANNEL.sendToServer(new RCActionButton(Action.DIVIDE_2));
        }
        if (btn == this.r$DIVIDE_3) {
            RandomComplement.NET_CHANNEL.sendToServer(new RCActionButton(Action.DIVIDE_3));
        }
        if (btn == this.r$MULTIPLY_2) {
            RandomComplement.NET_CHANNEL.sendToServer(new RCActionButton(Action.MULTIPLY_2));
        }
        if (btn == this.r$MULTIPLY_3) {
            RandomComplement.NET_CHANNEL.sendToServer(new RCActionButton(Action.MULTIPLY_3));
        }
    }

}
