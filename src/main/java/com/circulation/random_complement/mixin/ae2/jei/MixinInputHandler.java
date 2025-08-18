package com.circulation.random_complement.mixin.ae2.jei;

import mezz.jei.gui.overlay.bookmarks.LeftAreaDispatcher;
import mezz.jei.input.InputHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = InputHandler.class,remap = false)
public class MixinInputHandler {

    @Redirect(method = "<init>",at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    public boolean onInit(List<Object> instance, Object e){
        if (e instanceof LeftAreaDispatcher l){
            com.circulation.random_complement.client.handler.InputHandler.leftAreaDispatcher = l;
        }
        return instance.add(e);
    }
}
