package com.circulation.random_complement.mixin.ftblib;

import com.circulation.random_complement.common.interfaces.FakeInventoryEffectRenderer;
import com.feed_the_beast.ftblib.client.FTBLibClientEventHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = FTBLibClientEventHandler.class,remap = false)
public class MixinFTBLibClientEventHandler {

    @WrapOperation(method = "onGuiInit", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/event/GuiScreenEvent$InitGuiEvent$Post;getGui()Lnet/minecraft/client/gui/GuiScreen;"))
    private static GuiScreen onGuiInit(GuiScreenEvent.InitGuiEvent.Post instance, Operation<GuiScreen> original) {
        GuiScreen gui;
        if ((gui = original.call(instance)) instanceof FakeInventoryEffectRenderer f) {
            return f.r$getFakeGui();
        }
        return gui;
    }

    @WrapOperation(method = "onGuiInit", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/event/GuiScreenEvent$InitGuiEvent$Post;getButtonList()Ljava/util/List;"))
    private static List<GuiButton> onGuiInitGui(GuiScreenEvent.InitGuiEvent.Post instance, Operation<List<GuiButton>> original) {
        if (instance.getGui() instanceof FakeInventoryEffectRenderer f) {
            return f.r$getButtonList();
        }
        return original.call(instance);
    }

    @WrapOperation(method = "areButtonsVisible", at = @At(value = "CONSTANT", args = "classValue=net/minecraft/client/renderer/InventoryEffectRenderer", remap = true))
    private static boolean areButtonsVisible(Object object, Operation<Boolean> original) {
        return original.call(object) || object instanceof FakeInventoryEffectRenderer;
    }

}