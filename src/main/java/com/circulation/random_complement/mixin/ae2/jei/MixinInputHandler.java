package com.circulation.random_complement.mixin.ae2.jei;

import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.container.implementations.ContainerCraftAmount;
import appeng.container.implementations.ContainerCraftConfirm;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.KeyBindings;
import com.circulation.random_complement.client.handler.ItemTooltipHandler;
import com.circulation.random_complement.client.handler.RCInputHandler;
import com.circulation.random_complement.client.handler.ShortcutKeyState;
import com.circulation.random_complement.common.network.KeyBindingHandler;
import com.circulation.random_complement.common.util.Functions;
import com.circulation.random_complement.common.util.MEHandler;
import com.circulation.random_complement.mixin.jei.AccessorBookmarkItem;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import lombok.val;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.ghost.GhostIngredientDragManager;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.gui.overlay.bookmarks.LeftAreaDispatcher;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.InputHandler;
import mezz.jei.input.MouseHelper;
import mezz.jei.runtime.JeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = InputHandler.class, remap = false)
public abstract class MixinInputHandler {

    @Unique
    private final KeyBindings[] rc$keys = new KeyBindings[]{KeyBindings.StartCraft, KeyBindings.RetrieveItem};
    @Unique
    private final ShortcutKeyState rc$keyState = new ShortcutKeyState();

    @Unique
    public List<String> getRc$keyTooltips() {
        return Functions.asList(KeyBindings.RetrieveItem.getTooltip(), KeyBindings.StartCraft.getTooltip());
    }

    @Shadow
    @Nullable
    protected abstract IClickedIngredient<?> getFocusUnderMouseForClick(int mouseX, int mouseY);

    @Shadow
    protected abstract boolean hasKeyboardFocus();

    @Shadow
    protected abstract boolean isContainerTextFieldFocused();

    @Unique
    @SubscribeEvent
    public void rc$onGuiChanged(GuiOpenEvent event) {
        rc$keyState.clear();
    }

    @Inject(method = "onGuiKeyboardEvent(Lnet/minecraftforge/client/event/GuiScreenEvent$KeyboardInputEvent$Pre;)V",
        at = @At("HEAD"), cancellable = true, require = 1)
    private void rc$onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event, CallbackInfo ci) {
        boolean handled = rc$keyState.handle(Keyboard.getEventKey(), Keyboard.getEventKeyState(), Keyboard.isRepeatEvent(),
            () -> !hasKeyboardFocus() && !isContainerTextFieldFocused()
                && rc$work(Keyboard.getEventKey(), MouseHelper.getX(), MouseHelper.getY()));
        if (handled) {
            event.setCanceled(true);
            ci.cancel();
        }
    }

    // Run before HEI creates a drag/pending recipe click. HEI still tracks the matching release.
    @Inject(method = "handleMouseClick", at = @At("HEAD"), cancellable = true, require = 1)
    private void rc$onMouseClick(GuiScreen guiScreen, int mouseButton, int mouseX, int mouseY,
                                @Nullable IClickedIngredient<?> pendingClick, CallbackInfoReturnable<Boolean> cir) {
        if (pendingClick == null && rc$work(mouseButton - 100, mouseX, mouseY)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(JeiRuntime runtime, IngredientRegistry ingredientRegistry, IngredientListOverlay ingredientListOverlay, GuiScreenHelper guiScreenHelper, LeftAreaDispatcher leftAreaDispatcher, BookmarkList bookmarkList, GhostIngredientDragManager ghostIngredientDragManager, CallbackInfo ci) {
        ItemTooltipHandler.regItemTooltip(GuiScreen.class, () -> {
            val ing = getFocusUnderMouseForClick(MouseHelper.getX(), MouseHelper.getY());
            if (ing == null) return ObjectLists.emptyList();
            return getRc$keyTooltips();
        });
    }

    @Unique
    private boolean rc$work(int eventKey, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null || minecraft.currentScreen == null) return false;
        for (KeyBindings kb : rc$keys) {
            var k = kb.getKeyBinding();
            if (k.isActiveAndMatches(eventKey)) {
                if (kb.isNeedItem()) {
                    var ing = getFocusUnderMouseForClick(mouseX, mouseY);
                    if (ing == null) return false;
                    final ItemStack item;
                    if (ing.getValue() instanceof AccessorBookmarkItem<?> book) {
                        item = MEHandler.packItem(book.i_getIngredient());
                    } else {
                        item = MEHandler.packItem(ing.getValue());
                    }
                    if (item.isEmpty()) return false;
                    boolean isAE = minecraft.currentScreen instanceof GuiMEMonitorable;
                    if (kb == KeyBindings.StartCraft) {
                        var player = minecraft.player;
                        if (player.openContainer instanceof ContainerCraftAmount
                            || player.openContainer instanceof ContainerCraftConfirm) return true;
                        RCInputHandler.setDelayMethod(null);
                        // AE terminals already have their own return path; only wireless requests need this.
                        RCInputHandler.setOldGui(isAE ? null : minecraft.currentScreen);
                    }
                    RandomComplement.NET_CHANNEL.sendToServer(new KeyBindingHandler(kb.name(), item, isAE));
                    ing.onClickHandled();
                    return true;
                }
            }
        }
        return false;
    }
}
