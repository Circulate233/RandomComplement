package com.circulation.random_complement.client;

import lombok.Getter;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum KeyBindings {
    RetrieveItem(new KeyBinding("key.retrieve_item.desc", KeyConflictContext.GUI, KeyModifier.CONTROL, -98, ClientProxy.categoryJEI), true),
    StartCraft(new KeyBinding("key.start_craft.desc", KeyConflictContext.GUI, KeyModifier.ALT, -98, ClientProxy.categoryJEI), true),
    QueryInterface(new KeyBinding("key.query_interface.desc", KeyConflictContext.GUI, KeyModifier.SHIFT, -100, ClientProxy.categoryAE2), true);

    @Getter
    private final KeyBinding keyBinding;
    @Getter
    private final boolean needItem;
    private final String tooltip;

    KeyBindings(KeyBinding keyBinding, boolean needItem) {
        this.keyBinding = keyBinding;
        this.needItem = needItem;
        this.tooltip = keyBinding.getKeyDescription() + ".tooltip";
    }

    public static KeyBindings getKeyFromID(int id) {
        return KeyBindings.values()[id];
    }

    public static void init() {
        for (KeyBindings k : KeyBindings.values()) {
            ClientRegistry.registerKeyBinding(k.keyBinding);
        }
    }

    public int getID() {
        return this.ordinal();
    }

    public String getTooltip() {
        return I18n.format(this.tooltip, this.keyBinding.getKeyModifier().getLocalizedComboName(this.keyBinding.getKeyCode()));
    }
}