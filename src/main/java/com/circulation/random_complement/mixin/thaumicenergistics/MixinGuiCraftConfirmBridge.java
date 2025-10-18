package com.circulation.random_complement.mixin.thaumicenergistics;

import com.circulation.random_complement.mixin.ae2.gui.MixinGuiCraftConfirm;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import thaumicenergistics.client.gui.crafting.GuiCraftConfirmBridge;

import java.util.List;

@Mixin(value = GuiCraftConfirmBridge.class, priority = 999)
public abstract class MixinGuiCraftConfirmBridge extends MixinGuiCraftConfirm {

    @Unique
    private GuiButton r$cancel;

    public MixinGuiCraftConfirmBridge(Container container) {
        super(container);
    }

    @Override
    protected GuiButton r$getCancel() {
        return this.r$cancel;
    }

    @WrapOperation(method = "initGui", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    public boolean onInit(List<?> instance, Object e, Operation<Boolean> original) {
        return original.call(instance, this.r$cancel = (GuiButton) e);
    }
}