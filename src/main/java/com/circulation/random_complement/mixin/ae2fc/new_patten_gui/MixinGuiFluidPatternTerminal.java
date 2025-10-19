package com.circulation.random_complement.mixin.ae2fc.new_patten_gui;

import com.glodblock.github.client.GuiFluidPatternTerminal;
import com.glodblock.github.client.button.GuiFCImgButton;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiFluidPatternTerminal.class,remap = false)
public class MixinGuiFluidPatternTerminal {

    @Redirect(method = "drawFG", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lcom/glodblock/github/client/button/GuiFCImgButton;visible:Z", remap = true))
    public void r$serVisible(GuiFCImgButton instance, boolean value) {
        if (instance != null) {
            instance.visible = value;
        }
    }

}