package com.circulation.random_complement.mixin.enderutilities.ftblib;

import com.circulation.random_complement.common.interfaces.FakeInventoryEffectRenderer;
import fi.dy.masa.enderutilities.gui.client.GuiHandyBag;
import fi.dy.masa.enderutilities.gui.client.base.GuiContainerLargeStacks;
import fi.dy.masa.enderutilities.inventory.container.base.ContainerEnderUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(GuiHandyBag.class)
public abstract class MixinGuiHandyBag extends GuiContainerLargeStacks implements FakeInventoryEffectRenderer {

    @Unique
    private InventoryEffectRenderer r$fakeGui = new GuiInventory(Minecraft.getMinecraft().player);

    public MixinGuiHandyBag(ContainerEnderUtilities container, int xSize, int ySize, String textureName) {
        super(container, xSize, ySize, textureName);
    }

    @Override
    public InventoryEffectRenderer r$getFakeGui() {
        return r$fakeGui;
    }

    @Override
    public List<GuiButton> r$getButtonList() {
        return this.buttonList;
    }
}