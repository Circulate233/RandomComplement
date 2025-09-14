package com.circulation.random_complement.mixin.ae2.gui;

import appeng.api.implementations.IUpgradeableHost;
import appeng.client.gui.implementations.GuiInterface;
import appeng.client.gui.implementations.GuiUpgradeable;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.RCGuiButton;
import com.circulation.random_complement.client.RCSettings;
import com.circulation.random_complement.client.buttonsetting.Action;
import com.circulation.random_complement.client.buttonsetting.IntelligentBlocking;
import com.circulation.random_complement.common.interfaces.InterfaceConfigs;
import com.circulation.random_complement.common.network.RCActionButton;
import com.circulation.random_complement.common.network.RCConfigButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiInterface.class)
public abstract class MixinGuiInterface extends GuiUpgradeable {

    @Unique
    private RCGuiButton r$IntelligentBlocking;
    @Unique
    private RCGuiButton r$DIVIDE_2;
    @Unique
    private RCGuiButton r$DIVIDE_3;
    @Unique
    private RCGuiButton r$MULTIPLY_2;
    @Unique
    private RCGuiButton r$MULTIPLY_3;

    public MixinGuiInterface(InventoryPlayer inventoryPlayer, IUpgradeableHost te) {
        super(inventoryPlayer, te);
    }

    @Inject(method = "initGui",at = @At("TAIL"))
    public void onInitGui(CallbackInfo ci) {
        int i = 0;
        final int left = this.guiLeft - 18;
        final int top = r$getTop();
        this.r$IntelligentBlocking = new RCGuiButton(left, top + ++i * 18, RCSettings.IntelligentBlocking, IntelligentBlocking.CLOSE);
        this.buttonList.add(this.r$IntelligentBlocking);
        this.r$MULTIPLY_2 = new RCGuiButton(left, top + ++i * 18, RCSettings.ACTIONS, Action.MULTIPLY_2);
        this.buttonList.add(this.r$MULTIPLY_2);
        this.r$DIVIDE_2 = new RCGuiButton(left, top + ++i * 18, RCSettings.ACTIONS, Action.DIVIDE_2);
        this.buttonList.add(this.r$DIVIDE_2);
        this.r$MULTIPLY_3 = new RCGuiButton(left, top + ++i * 18, RCSettings.ACTIONS, Action.MULTIPLY_3);
        this.buttonList.add(this.r$MULTIPLY_3);
        this.r$DIVIDE_3 = new RCGuiButton(left, top + ++i * 18, RCSettings.ACTIONS, Action.DIVIDE_3);
        this.buttonList.add(this.r$DIVIDE_3);
    }

    @Unique
    public int r$getTop(){
        int top = this.guiTop + 8;
        final int left = this.guiLeft - 18;
        for (GuiButton guiButton : this.buttonList) {
            if (guiButton.x != left)continue;
            if (top < guiButton.y)top = guiButton.y;
        }
        return top;
    }

    @Inject(method = "drawFG", at = @At("HEAD"), remap = false)
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        this.r$IntelligentBlocking.set(((InterfaceConfigs) this.cvb).r$getIntelligentBlocking());
    }

    @Inject(method = "actionPerformed",at = @At("HEAD"), cancellable = true)
    protected void actionPerformed(GuiButton btn, CallbackInfo ci) {
        boolean backwards = Mouse.isButtonDown(1);
        if (btn == this.r$IntelligentBlocking) {
            var option = this.r$IntelligentBlocking.getRCSetting();
            RandomComplement.NET_CHANNEL.sendToServer(new RCConfigButton(option, backwards));
            ci.cancel();
        }
        if (btn == this.r$DIVIDE_2) {
            RandomComplement.NET_CHANNEL.sendToServer(new RCActionButton(Action.DIVIDE_2));
            ci.cancel();
        }
        if (btn == this.r$DIVIDE_3) {
            RandomComplement.NET_CHANNEL.sendToServer(new RCActionButton(Action.DIVIDE_3));
            ci.cancel();
        }
        if (btn == this.r$MULTIPLY_2) {
            RandomComplement.NET_CHANNEL.sendToServer(new RCActionButton(Action.MULTIPLY_2));
            ci.cancel();
        }
        if (btn == this.r$MULTIPLY_3) {
            RandomComplement.NET_CHANNEL.sendToServer(new RCActionButton(Action.MULTIPLY_3));
            ci.cancel();
        }
    }

}
