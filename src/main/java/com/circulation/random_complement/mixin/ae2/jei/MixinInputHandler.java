package com.circulation.random_complement.mixin.ae2.jei;

import appeng.client.gui.implementations.GuiMEMonitorable;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.common.network.KeyBindingHandle;
import com.circulation.random_complement.common.util.Function;
import com.glodblock.github.common.item.fake.FakeFluids;
import com.glodblock.github.integration.mek.FakeGases;
import mekanism.api.gas.GasStack;
import mezz.jei.gui.overlay.bookmarks.LeftAreaDispatcher;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.InputHandler;
import mezz.jei.input.MouseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.circulation.random_complement.client.KeyBindings.*;

@Mixin(value = InputHandler.class,remap = false)
public class MixinInputHandler {

    @Unique
    public LeftAreaDispatcher randomComplement$leftAreaDispatcher;

    @Redirect(method = "<init>",at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    public boolean onInit(List<Object> instance, Object e){
        if (e instanceof LeftAreaDispatcher l){
            randomComplement$leftAreaDispatcher = l;
        }
        return instance.add(e);
    }

    @Inject(method = "onGuiKeyboardEvent(Lnet/minecraftforge/client/event/GuiScreenEvent$KeyboardInputEvent$Post;)V",at = @At("HEAD"), cancellable = true)
    public void onGuiKeyboardEventPreMixin(GuiScreenEvent.KeyboardInputEvent.Post event, CallbackInfo ci) {
        if (randomComplement$work(false)){
            event.setCanceled(true);
            ci.cancel();
        }
    }

    @Inject(method = "onGuiMouseEvent",at = @At("HEAD"), cancellable = true)
    public void onGuiMouseEventMixin(GuiScreenEvent.MouseInputEvent.Pre event, CallbackInfo ci) {
        if (randomComplement$work(true)){
            event.setCanceled(true);
            ci.cancel();
        }
    }

    @Unique
    boolean randomComplement$work = true;

    @SubscribeEvent
    @Unique
    public void r$onTick(TickEvent.ServerTickEvent event){
        if (!randomComplement$work){
            randomComplement$work = true;
        }
    }

    @Unique
    private boolean randomComplement$work(boolean isMouse){
        int eventKey;
        int m = 0;
        if (isMouse){
            m = Mouse.getEventButton();
            eventKey = m - 100;
        } else {
            eventKey = Keyboard.getEventKey();
        }
        for (KeyBinding k : allKeyBinding) {
            if (k.isActiveAndMatches(eventKey)
            && k.getKeyModifier().isActive(k.getKeyConflictContext())) {
                if (isMouse && !Mouse.isButtonDown(m)){
                    return true;
                }
                if (randomComplement$work){
                    randomComplement$work = false;
                } else {
                    return true;
                }
                var ing = randomComplement$leftAreaDispatcher.getIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
                if (ing == null)return false;
                ItemStack item = ItemStack.EMPTY;
                if (ing.getValue() instanceof ItemStack i){
                    item = i.copy();
                } else if (Function.modLoaded("ae2fc")){
                    item = randomComplement$ae2fcWork(ing);
                }
                if (item.isEmpty())return false;
                if (k == RetrieveItem.getKeyBinding()) {
                    RandomComplement.NET_CHANNEL.sendToServer(new KeyBindingHandle(RetrieveItem.name(),item, Minecraft.getMinecraft().currentScreen instanceof GuiMEMonitorable));
                } else if (k == StartCraft.getKeyBinding()) {
                    RandomComplement.NET_CHANNEL.sendToServer(new KeyBindingHandle(StartCraft.name(),item, Minecraft.getMinecraft().currentScreen instanceof GuiMEMonitorable));
                }
                return true;
            }
        }
        return false;
    }

    @Unique
    @Optional.Method(modid = "ae2fc")
    public ItemStack randomComplement$ae2fcWork(IClickedIngredient<?> ing){
        if (ing.getValue() instanceof FluidStack i) {
            var ii = FakeFluids.packFluid2Drops(i);
            if (ii != null){
                return ii;
            }
        } else if (Function.modLoaded("mekeng")){
            return randomComplement$mekengWork(ing);
        }
        return ItemStack.EMPTY;
    }

    @Unique
    @Optional.Method(modid = "mekeng")
    private ItemStack randomComplement$mekengWork(IClickedIngredient<?> ing){
        if (ing.getValue() instanceof GasStack i) {
            var ii = FakeGases.packGas2Drops(i);
            if (ii != null){
                return ii;
            }
        }
        return ItemStack.EMPTY;
    }
}
