package com.circulation.random_complement.mixin.ae2.jei;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import mezz.jei.config.Config;
import mezz.jei.gui.overlay.bookmarks.group.BookmarkGroupDisplay;
import mezz.jei.gui.overlay.bookmarks.group.BookmarkGroupOrganizer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = BookmarkGroupOrganizer.class, remap = false, priority = 1001)
public abstract class MixinBookmarkGroupOrganizer {

    @Mutable
    @Shadow
    @Final
    private List<BookmarkGroupDisplay> groups;

    @Shadow
    protected abstract void drawGroup(Minecraft minecraft, int mouseX, int mouseY, BookmarkGroupDisplay display);

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        groups = ObjectLists.synchronize(new ObjectArrayList<>());
    }

    /**
     * @author circulation
     * @reason 测试性修改
     */
    @Overwrite
    public void draw(Minecraft minecraft, int mouseX, int mouseY) {
        if (Config.areRecipeBookmarksEnabled()) {
            for (var i = 0; i < this.groups.size(); i++) {
                this.drawGroup(minecraft, mouseX, mouseY, this.groups.get(i));
            }
        }
    }

}
