package com.circulation.random_complement.common.integration.ae2.core;

import net.minecraft.util.text.translation.I18n;

public interface localizationAPI {

    String getRoot();

    default String getLocal() {
        return I18n.translateToLocal(this.getUnlocalized());
    }

    default String getUnlocalized() {
        return this.getRoot() + '.' + this;
    }
}
