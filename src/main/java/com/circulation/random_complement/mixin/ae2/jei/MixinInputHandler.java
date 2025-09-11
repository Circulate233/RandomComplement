package com.circulation.random_complement.mixin.ae2.jei;

import appeng.client.gui.implementations.GuiMEMonitorable;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.KeyBindings;
import com.circulation.random_complement.client.handler.RCInputHandler;
import com.circulation.random_complement.common.network.KeyBindingHandler;
import com.circulation.random_complement.common.util.Function;
import com.glodblock.github.common.item.fake.FakeFluids;
import com.glodblock.github.integration.mek.FakeGases;
import lombok.val;
import mekanism.api.gas.GasStack;
import mezz.jei.bookmarks.BookmarkItem;
import mezz.jei.gui.overlay.bookmarks.LeftAreaDispatcher;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.InputHandler;
import mezz.jei.input.MouseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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

@Mixin(value = InputHandler.class,remap = false)
public class MixinInputHandler {

    @Shadow
    @Final
    private LeftAreaDispatcher leftAreaDispatcher;

    @Unique
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void r$onGuiKeyboardEventPre(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (r$work(false)) {
            event.setCanceled(true);
        }
    }

    @Unique
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void r$onGuiMouseEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (r$work(true)) {
            event.setCanceled(true);
        }
    }

    @Unique
    @SubscribeEvent
    public void r$onItemTooltip(ItemTooltipEvent event) {
        val ing = leftAreaDispatcher.getIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
        if (ing == null) return;
        if (GuiScreen.isAltKeyDown()) {
            event.getToolTip().addAll(KeyBindings.getTooltipList());
        } else {
            event.getToolTip().add(I18n.format("hei.tooltip.press_alt"));
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
                    } else if (Function.modLoaded("ae2fc")) {
                        item = r$ae2fcWork(ing);
                    }
                    if (item.isEmpty()) return false;
                    final var oldGui = Minecraft.getMinecraft().currentScreen;

                    RandomComplement.NET_CHANNEL.sendToServer(new KeyBindingHandler(kb.name(), item, Minecraft.getMinecraft().currentScreen instanceof GuiMEMonitorable));

                    if (kb == KeyBindings.StartCraft) {
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
        } else if (Function.modLoaded("mekeng")) {
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
        }
        return ItemStack.EMPTY;
    }
}