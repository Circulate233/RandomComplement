package com.circulation.random_complement.common.interfaces;

import appeng.client.gui.widgets.GuiScrollbar;

public interface RCPatternEncoder {

    void setRCSlot(int i);

    void setRCSlot();

    boolean isRCPatternEncoder();

    void setScrollbar(GuiScrollbar scrollbar);
}