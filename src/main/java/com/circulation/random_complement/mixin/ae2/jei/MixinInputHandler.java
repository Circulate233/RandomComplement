package com.circulation.random_complement.mixin.ae2.jei;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.container.implementations.ContainerCraftAmount;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.integration.modules.jei.JEIPlugin;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.KeyBindings;
import com.circulation.random_complement.client.handler.ItemTooltipHandler;
import com.circulation.random_complement.client.handler.RCInputHandler;
import com.circulation.random_complement.client.handler.RCJEIInputHandler;
import com.circulation.random_complement.common.network.KeyBindingHandler;
import com.circulation.random_complement.common.util.Functions;
import com.circulation.random_complement.common.util.MEHandler;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import lombok.Getter;
import lombok.val;
import mezz.jei.bookmarks.BookmarkItem;
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
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = InputHandler.class, remap = false)
public abstract class MixinInputHandler {

    @Shadow
    @Nullable
    protected abstract IClickedIngredient<?> getFocusUnderMouseForClick(int mouseX, int mouseY);

    @Unique
    private final KeyBindings[] rc$keys = new KeyBindings[]{KeyBindings.StartCraft, KeyBindings.RetrieveItem};
    @Unique
    @Getter(lazy = true)
    private final List<String> rc$keyTooltips = Functions.asList(KeyBindings.RetrieveItem.getTooltip(), KeyBindings.StartCraft.getTooltip());

    @Unique
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void r$onClickEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Minecraft.getMinecraft().currentScreen instanceof AEBaseGui gui) {
            int eventButton = Mouse.getEventButton();
            if (eventButton > -1) {
                if (Mouse.isButtonDown(eventButton)) {
                    var ing = getFocusUnderMouseForClick(MouseHelper.getX(), MouseHelper.getY());
                    if (ing == null) return;
                    RCJEIInputHandler.setShiftClickCache(() ->
                        JEIPlugin.aeGuiHandler.getTargets(gui, ing.getValue(), true));
                }
            }
        }
    }

    @Unique
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void r$onGuiKeyboardEventPre(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (r$work(false)) {
            event.setCanceled(true);
        }
    }

    @Unique
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void r$onGuiMouseEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (r$work(true)) {
            event.setCanceled(true);
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
    private boolean r$work(boolean isMouse) {
        int eventKey;
        int m = 0;
        if (isMouse) {
            m = Mouse.getEventButton();
            eventKey = m - 100;
        } else {
            eventKey = Keyboard.getEventKey();
        }
        for (KeyBindings kb : rc$keys) {
            var k = kb.getKeyBinding();
            if (k.isActiveAndMatches(eventKey)) {
                if (kb.isNeedItem()) {
                    if (isMouse && !Mouse.isButtonDown(m)) {
                        return true;
                    }
                    var ing = getFocusUnderMouseForClick(MouseHelper.getX(), MouseHelper.getY());
                    if (ing == null) return false;
                    final ItemStack item;
                    if (ing.getValue() instanceof BookmarkItem<?> book) {
                        item = MEHandler.packItem(book.ingredient);
                    } else {
                        item = MEHandler.packItem(ing.getValue());
                    }
                    if (item.isEmpty()) return false;
                    final var oldGui = Minecraft.getMinecraft().currentScreen;

                    RandomComplement.NET_CHANNEL.sendToServer(new KeyBindingHandler(kb.name(), item, Minecraft.getMinecraft().currentScreen instanceof GuiMEMonitorable));

                    if (kb == KeyBindings.StartCraft) {
                        var player = Minecraft.getMinecraft().player;
                        if (player.openContainer instanceof ContainerCraftAmount
                            || player.openContainer instanceof ContainerCraftConfirm) return false;
                        RCInputHandler.setOldGui(oldGui);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}