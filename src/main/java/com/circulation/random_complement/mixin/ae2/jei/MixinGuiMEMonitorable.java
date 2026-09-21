package com.circulation.random_complement.mixin.ae2.jei;

import appeng.client.gui.AEBaseMEGui;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.widgets.MEGuiTextField;
import appeng.client.me.ItemRepo;
import com.circulation.random_complement.common.interfaces.RCGuiMEMonitorableJei;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.Rectangle;

@Mixin(value = GuiMEMonitorable.class)
public abstract class MixinGuiMEMonitorable extends AEBaseMEGui implements RCGuiMEMonitorableJei {

    @Shadow(remap = false)
    @Final
    protected ItemRepo repo;
    @Shadow(remap = false)
    private MEGuiTextField searchField;

    public MixinGuiMEMonitorable(Container container) {
        super(container);
    }

    @Override
    public IGhostIngredientHandler.Target<?> r$getMEGuiTextFieldTarget() {
        if (searchField == null) return null;
        return new IGhostIngredientHandler.Target<>() {
            @Override
            public @NotNull Rectangle getArea() {
                return new Rectangle(searchField.x - 2, searchField.y - 2, searchField.width + 4 + fontRenderer.getCharWidth('_'), searchField.height + 4);
            }

            @Override
            public void accept(@NotNull Object o) {
                if (o instanceof ItemStack mouseItem) {
                    if (!mouseItem.isEmpty()) {
                        String name = mouseItem.getDisplayName();
                        searchField.setText(name);
                        repo.setSearchString(name);
                    }
                }
            }
        };
    }
}