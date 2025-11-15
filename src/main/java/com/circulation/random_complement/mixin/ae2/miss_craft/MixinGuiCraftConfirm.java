package com.circulation.random_complement.mixin.ae2.miss_craft;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.implementations.GuiCraftConfirm;
import appeng.container.implementations.ContainerCraftConfirm;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiCraftConfirm.class, remap = false)
public abstract class MixinGuiCraftConfirm {

    @Shadow
    @Final
    private IItemList<IAEItemStack> missing;

    @Shadow
    private GuiButton start;

    @Shadow
    @Final
    private ContainerCraftConfirm ccc;

    @Inject(method = "postUpdate", at = @At("TAIL"))
    public void postUpdate(List<IAEItemStack> list, byte ref, CallbackInfo ci) {
        if (ref == 2 && !this.missing.isEmpty() && !this.ccc.noCPU) {
            this.start.x -= 20;
            this.start.width += 20;
            this.start.displayString = I18n.format("gui.rc.miss_craft.text");
        }
    }
}
