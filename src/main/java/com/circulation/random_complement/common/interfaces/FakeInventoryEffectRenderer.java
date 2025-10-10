package com.circulation.random_complement.common.interfaces;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.InventoryEffectRenderer;

import java.util.List;

public interface FakeInventoryEffectRenderer {

    InventoryEffectRenderer r$getFakeGui();

    List<GuiButton> r$getButtonList();
}