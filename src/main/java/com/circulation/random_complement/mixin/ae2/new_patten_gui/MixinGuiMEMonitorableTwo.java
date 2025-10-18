package com.circulation.random_complement.mixin.ae2.new_patten_gui;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.widgets.ISortSource;
import appeng.client.me.ItemRepo;
import com.circulation.random_complement.client.RCGuiScrollbar;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.inventory.Container;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;

@Mixin(value = GuiMEMonitorable.class, remap = false)
public abstract class MixinGuiMEMonitorableTwo extends AEBaseGui implements ISortSource {

    @Unique
    private final List<RCGuiScrollbar> r$guiScrollbars = new ObjectArrayList<>();
    @Mutable
    @Shadow
    @Final
    protected ItemRepo repo;

    public MixinGuiMEMonitorableTwo(Container container) {
        super(container);
    }

    @Shadow
    protected abstract String getBackground();

    @Unique
    @NotNull
    protected List<RCGuiScrollbar> r$getScrollBars() {
        return r$guiScrollbars;
    }

    @Unique
    protected void r$addScrollBars(){

    }

    @Inject(method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lappeng/api/storage/ITerminalHost;Lappeng/container/implementations/ContainerMEMonitorable;)V", at = @At("TAIL"))
    public void onInitGui(CallbackInfo ci) {
        r$addScrollBars();
        if (!r$getScrollBars().isEmpty()) {
            RCGuiScrollbar scrollbar = new RCGuiScrollbar();
            scrollbar.setLeftEx(175);
            scrollbar.setWidthEx(175 + 8);
            scrollbar.setWidth(scrollbar.getWidth());
            this.setScrollBar(scrollbar);
            this.repo = new ItemRepo(scrollbar, this);
        }
    }

    @Unique
    protected void r$setScrollBar() {

    }

    @Inject(method = "setScrollBar", at = @At("TAIL"))
    private void setScrollBar(CallbackInfo ci) {
        if (!r$getScrollBars().isEmpty()) {
            r$setScrollBar();
        }
    }

    @Inject(method = "drawFG", at = @At("HEAD"))
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        for (var guiScrollbar : r$guiScrollbars) {
            guiScrollbar.draw(this);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    protected void mouseClicked(int xCoord, int yCoord, int btn, CallbackInfo ci) {
        for (var guiScrollbar : r$guiScrollbars) {
            guiScrollbar.click(this, xCoord - this.guiLeft, yCoord - this.guiTop);
        }
    }

    @Intrinsic
    @Override
    protected void mouseClickMove(int x, int y, int c, long d) {
        for (var guiScrollbar : r$guiScrollbars) {
            guiScrollbar.click(this, x - this.guiLeft, y - this.guiTop);
        }
        super.mouseClickMove(x, y, c, d);
    }

    @Intrinsic
    @Override
    public void handleMouseInput() throws IOException {
        int i = Mouse.getEventDWheel();
        if (i != 0) {
            for (var guiScrollbar : r$guiScrollbars) {
                guiScrollbar.wheel(i);
            }
        }
        super.handleMouseInput();
    }

    @Inject(method = "drawBG", at = @At("TAIL"))
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        if (r$getScrollBars().isEmpty()) return;
        this.bindTexture(this.getBackground());
        for (var scrollbar : r$guiScrollbars) {
            if (scrollbar.isVisible()) {
                scrollbar.drawBG();
            }
        }
    }
}