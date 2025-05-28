package com.circulation.random_complement.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public enum KeyBindings {
    RetrieveItem(new KeyBinding("key.retrieve_item.desc", KeyConflictContext.GUI, KeyModifier.CONTROL,-98,ClientProxy.categoryJEI),true),
    StartCraft(new KeyBinding("key.start_craft.desc", KeyConflictContext.GUI, KeyModifier.ALT, -98, ClientProxy.categoryJEI),true);

    private final KeyBinding keyBinding;
    private final boolean needItem;

    KeyBindings(KeyBinding keyBinding,boolean needItem) {
        this.keyBinding = keyBinding;
        this.needItem = needItem;
    }

    public KeyBinding getKeyBinding() {
        return this.keyBinding;
    }

    public boolean needItem() {
        return this.needItem;
    }
}