package com.circulation.random_complement.common.interfaces;

import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraftforge.fml.common.Optional;

public interface RCGuiMEMonitorableJei {

    @Optional.Method(modid = "jei")
    IGhostIngredientHandler.Target<?> r$getMEGuiTextFieldTarget();

}
