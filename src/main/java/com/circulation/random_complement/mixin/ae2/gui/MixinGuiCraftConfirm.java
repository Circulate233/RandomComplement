package com.circulation.random_complement.mixin.ae2.gui;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiCraftConfirm;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketValueConfig;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.RandomComplement;
import com.circulation.random_complement.client.handler.RCInputHandler;
import com.circulation.random_complement.common.network.ContainerRollBACK;
import com.circulation.random_complement.common.util.AEBookmarkGroup;
import com.google.common.base.Joiner;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.Internal;
import mezz.jei.bookmarks.BookmarkGroup;
import mezz.jei.bookmarks.BookmarkList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiCraftConfirm.class, priority = 999)
public abstract class MixinGuiCraftConfirm extends AEBaseGui {
    @Shadow(remap = false)
    private GuiButton cancel;
    @Shadow(remap = false)
    private GuiButton start;
    @Shadow(remap = false)
    @Final
    private IItemList<IAEItemStack> missing;

    public MixinGuiCraftConfirm(Container container) {
        super(container);
    }

    @Unique
    private static String randomComplement$getItemInformation(final Object o) {
        String dspToolTip = "";
        List<String> lineList = new ObjectArrayList<>();
        if (o == null) {
            return "** Null";
        }
        ITooltipFlag.TooltipFlags tooltipFlag = ITooltipFlag.TooltipFlags.NORMAL;

        try {
            tooltipFlag = Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
        } catch (final Exception ignored) {
        }

        ItemStack itemStack = ItemStack.EMPTY;
        if (o instanceof AEItemStack aeItemStack) {
            try {
                aeItemStack.getItem().addInformation(aeItemStack.getDefinition(), null, lineList, tooltipFlag);
            } catch (Exception ignored) {
            }
        } else if (o instanceof ItemStack stack) {
            itemStack = stack;
        } else {
            return "";
        }

        try {
            itemStack.getItem().addInformation(itemStack, null, lineList, tooltipFlag);
        } catch (Exception ignored) {
        }

        if (!lineList.isEmpty()) {
            dspToolTip = dspToolTip + '\n' + Joiner.on("\n").join(lineList);
        }
        return dspToolTip;
    }

    @Unique
    protected GuiButton r$getCancel() {
        return this.cancel;
    }

    @Shadow(remap = false)
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {

    }

    @Inject(method = "drawFG", at = @At("TAIL"), remap = false)
    private void onDrawFG(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        if (r$getCancel().isMouseOver() && Loader.isModLoaded("jei")) {
            this.drawHoveringText(I18n.format("text.rc.confirm_cancel"), mouseX - offsetX, mouseY - offsetY);
        }
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    public void onActionPerformed1(GuiButton btn, CallbackInfo ci) {
        if (Loader.isModLoaded("jei") && btn == r$getCancel() && isShiftKeyDown()) rc$addMissBookmark();
        if (RCInputHandler.getOldGui() == null) return;
        if (btn == this.start || btn == r$getCancel()) {
            if (btn == this.start) {
                try {
                    NetworkHandler.instance().sendToServer(new PacketValueConfig("Terminal.Start", "Start"));
                } catch (Throwable e) {
                    AELog.debug(e);
                }
            }

            GuiScreen oldGui;
            if ((oldGui = RCInputHandler.getOldGui()) != null) {
                RCInputHandler.setDelayMethod(() -> Minecraft.getMinecraft().displayGuiScreen(oldGui));
                RandomComplement.NET_CHANNEL.sendToServer(new ContainerRollBACK());
            }
            ci.cancel();
        }
    }

    @Unique
    @Optional.Method(modid = "jei")
    public void rc$addMissBookmark() {
        BookmarkList bookmarkList = Internal.getBookmarkList();
        BookmarkGroup group = new AEBookmarkGroup(bookmarkList.nextId(), this.missing);
        bookmarkList.add(group);
    }

    @WrapOperation(method = "drawFG", at = @At(value = "INVOKE", target = "Lappeng/util/Platform;getItemDisplayName(Ljava/lang/Object;)Ljava/lang/String;"), remap = false)
    public String addItemInformation(Object n, Operation<String> original) {
        String out;
        if (!(out = randomComplement$getItemInformation(n)).isEmpty()) {
            return original.call(n) + out;
        } else {
            return original.call(n);
        }
    }

    @Unique
    private String randomComplement$getItemDisplayName(Object n) {
        return Platform.getItemDisplayName(n);
    }
}