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
import com.glodblock.github.common.item.fake.FakeFluids;
import com.glodblock.github.integration.mek.FakeGases;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import lombok.val;
import mekanism.api.gas.GasStack;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = InputHandler.class, remap = false)
public abstract class MixinInputHandler {

    @Shadow
    @Final
    private LeftAreaDispatcher leftAreaDispatcher;

    @Shadow
    @Final
    private GhostIngredientDragManager ghostIngredientDragManager;

    @Shadow
    @Nullable
    protected abstract IClickedIngredient<?> getFocusUnderMouseForClick(int mouseX, int mouseY);

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(JeiRuntime runtime, IngredientRegistry ingredientRegistry, IngredientListOverlay ingredientListOverlay, GuiScreenHelper guiScreenHelper, LeftAreaDispatcher leftAreaDispatcher, BookmarkList bookmarkList, GhostIngredientDragManager ghostIngredientDragManager, CallbackInfo ci) {
        ItemTooltipHandler.regItemTooltip(GuiScreen.class, () -> {
            val ing = leftAreaDispatcher.getIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
            if (ing == null) return ObjectLists.emptyList();
            return KeyBindings.getTooltipList();
        });
    }

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
        for (KeyBindings kb : KeyBindings.values()) {
            var k = kb.getKeyBinding();
            if (k.isActiveAndMatches(eventKey)
                    && k.getKeyModifier().isActive(k.getKeyConflictContext())) {
                if (kb.isNeedItem()) {
                    var ing = leftAreaDispatcher.getIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
                    if (ing == null) return false;
                    if (isMouse && !Mouse.isButtonDown(m)) {
                        return true;
                    }
                    ItemStack item = ItemStack.EMPTY;
                    if (ing.getValue() instanceof BookmarkItem<?> book) {
                        if (book.ingredient instanceof ItemStack i) {
                            item = i;
                        }
                    } else if (ing.getValue() instanceof ItemStack i) {
                        item = i;
                    } else if (Functions.modLoaded("ae2fc")) {
                        item = r$ae2fcWork(ing);
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

    @Unique
    @Optional.Method(modid = "ae2fc")
    public ItemStack r$ae2fcWork(IClickedIngredient<?> ing) {
        if (ing.getValue() instanceof BookmarkItem<?> book) {
            if (book.ingredient instanceof FluidStack i) {
                var ii = FakeFluids.packFluid2Drops(i);
                if (ii != null) {
                    return ii;
                }
            }
        } else if (ing.getValue() instanceof FluidStack i) {
            var ii = FakeFluids.packFluid2Drops(i);
            if (ii != null) {
                return ii;
            }
        } else if (Functions.modLoaded("mekeng")) {
            return r$mekengWork(ing);
        }
        return ItemStack.EMPTY;
    }

    @Unique
    @Optional.Method(modid = "mekeng")
    private ItemStack r$mekengWork(IClickedIngredient<?> ing) {
        if (ing.getValue() instanceof BookmarkItem<?> book) {
            if (book.ingredient instanceof GasStack i) {
                var ii = FakeGases.packGas2Drops(i);
                if (ii != null) {
                    return ii;
                }
            }
        } else if (ing.getValue() instanceof GasStack i) {
            var ii = FakeGases.packGas2Drops(i);
            if (ii != null) {
                return ii;
            }
        }
        return ItemStack.EMPTY;
    }
}