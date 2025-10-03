package com.circulation.random_complement.mixin.ae2.jei;

import appeng.client.gui.AEBaseGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = AEBaseGui.class,remap = false)
public class JEIPluginMixin {

    /**
     * @author circulation
     * @reason 删除此部分，使用JEI自身实现进行处理
     */
    @Overwrite
    void bookmarkedJEIghostItem(int mouseX, int mouseY) {

    }
}
