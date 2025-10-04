package com.circulation.random_complement.mixin.de;

import com.brandon3055.draconicevolution.client.gui.GuiDislocator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiDislocator.class)
public abstract class MixinGuiDislocator extends GuiScreen {

    @Shadow(remap = false)
    private GuiTextField textBeingEdited;

    @Shadow(remap = false)
    private boolean editingNew;

    @Shadow(remap = false)
    private boolean editingExisting;

    @Shadow
    protected abstract void actionPerformed(@NotNull GuiButton button);

    /**
     * @author circulation
     * @reason 问号，为什么硬编码GUI关闭
     */
    @Overwrite
    public void keyTyped(char key, int keyN) {
        int b = -1;
        if (this.editingExisting) {
            b = 0;
        } else if (this.editingNew) {
            b = 4;
        }
        if (b >= 0) {
            if (this.textBeingEdited.textboxKeyTyped(key, keyN)) {
                this.buttonList.get(b).enabled = !this.textBeingEdited.getText().isEmpty();
                this.buttonList.get(b).displayString = I18n.format("button.de.commit.txt");

            } else if (keyN == 28) {
                this.actionPerformed(this.buttonList.get(b));
            }
            return;
        }

        if (keyN == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyN)) {
            this.mc.player.closeScreen();
        }
    }
}