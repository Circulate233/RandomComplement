package com.circulation.random_complement.mixin.ae2.gui;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiInscriber;
import appeng.container.implementations.ContainerInscriber;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.RCGuiButton;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.InscriberAutoOutput;
import com.circulation.random_complement.client.buttonsetting.InscriberBlockMode;
import com.circulation.random_complement.client.buttonsetting.InscriberMaxStackLimit;
import com.circulation.random_complement.common.interfaces.InscriberConfigs;
import com.circulation.random_complement.common.network.RCConfigButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(GuiInscriber.class)
public abstract class MixinGuiInscriber extends AEBaseGui {

    @Shadow(remap = false)
    @Final
    private ContainerInscriber cvc;

    @Unique
    private RCGuiButton randomComplement$BlockMode;
    @Unique
    private RCGuiButton randomComplement$AutoOutput;
    @Unique
    private RCGuiButton randomComplement$MaxStackLimit;

    public MixinGuiInscriber(Container container) {
        super(container);
    }

    @Inject(method = "initGui",at = @At("TAIL"))
    public void onInitGui(CallbackInfo ci) {
        int i = 0;
        this.randomComplement$BlockMode = new RCGuiButton(this.guiLeft - 18, this.guiTop + i++ * 20 + 8, RCSettings.InscriberBlockMode, InscriberBlockMode.CLOSE);
        this.buttonList.add(this.randomComplement$BlockMode);
        this.randomComplement$AutoOutput = new RCGuiButton(this.guiLeft - 18, this.guiTop + i++ * 20 + 8, RCSettings.InscriberAutoOutput, InscriberAutoOutput.CLOSE);
        this.buttonList.add(this.randomComplement$AutoOutput);
        this.randomComplement$MaxStackLimit = new RCGuiButton(this.guiLeft - 18, this.guiTop + i++ * 20 + 8, RCSettings.InscriberMaxStackLimit, InscriberMaxStackLimit.SMALL);
        this.buttonList.add(this.randomComplement$MaxStackLimit);
    }

    @Inject(method = "drawFG",at = @At("HEAD"),remap = false)
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        this.randomComplement$BlockMode.set(((InscriberConfigs)this.cvc).r$getBlockMode());
        this.randomComplement$AutoOutput.set(((InscriberConfigs)this.cvc).r$getAutoOutput());
        this.randomComplement$MaxStackLimit.set(((InscriberConfigs)this.cvc).r$getMaxStackLimit());
    }

    @Override
    @Unique
    protected void actionPerformed(@NotNull GuiButton btn) throws IOException {
        super.actionPerformed(btn);
        boolean backwards = Mouse.isButtonDown(1);
        if (btn == this.randomComplement$BlockMode) {
            var option = this.randomComplement$BlockMode.getRCSetting();
            RandomComplement.NET_CHANNEL.sendToServer(new RCConfigButton(option,backwards));
        }
        if (btn == this.randomComplement$AutoOutput) {
            var option = this.randomComplement$AutoOutput.getRCSetting();
            RandomComplement.NET_CHANNEL.sendToServer(new RCConfigButton(option,backwards));
        }
        if (btn == this.randomComplement$MaxStackLimit) {
            var option = this.randomComplement$MaxStackLimit.getRCSetting();
            RandomComplement.NET_CHANNEL.sendToServer(new RCConfigButton(option,backwards));
        }
    }

}
