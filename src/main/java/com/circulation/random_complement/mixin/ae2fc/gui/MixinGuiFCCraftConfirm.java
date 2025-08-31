package com.circulation.random_complement.mixin.ae2fc.gui;

import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.implementations.GuiCraftConfirm;
import com.circulation.random_complement.mixin.ae2.gui.AccessorGuiCraftConfirm;
import com.glodblock.github.client.GuiFCCraftConfirm;
import mezz.jei.Internal;
import mezz.jei.bookmarks.BookmarkGroup;
import mezz.jei.bookmarks.BookmarkItem;
import mezz.jei.bookmarks.BookmarkList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiFCCraftConfirm.class)
public class MixinGuiFCCraftConfirm extends GuiCraftConfirm {

    @Shadow(remap = false)
    private GuiButton cancel;

    public MixinGuiFCCraftConfirm(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(offsetX, offsetY, mouseX, mouseY);
        if (this.cancel.isMouseOver() && Loader.isModLoaded("jei")) {
            this.drawHoveringText(I18n.format("text.rc.confirm_cancel"),mouseX - offsetX,mouseY - offsetY);
        }
    }

    @Inject(
            method = "actionPerformed",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/glodblock/github/inventory/InventoryHandler;switchGui(Lcom/glodblock/github/inventory/GuiType;)V",
                    shift = At.Shift.AFTER,
                    remap = false
            ))
    public void onActionPerformed1(GuiButton btn, CallbackInfo ci) {
        if (Loader.isModLoaded("jei") && isShiftKeyDown())rc$addMissBookmark();
    }

    @Unique
    @Optional.Method(modid = "jei")
    private void rc$addMissBookmark() {
        BookmarkList bookmarkList = Internal.getBookmarkList();
        BookmarkGroup group = new BookmarkGroup(bookmarkList.nextId());
        for (IAEItemStack iaeItemStack : ((AccessorGuiCraftConfirm) this).getMissing()) {
            var item = new BookmarkItem<>(iaeItemStack.createItemStack());
            item.amount = iaeItemStack.getStackSize();
            group.addItem(item);
        }
        bookmarkList.add(group);
    }
}
