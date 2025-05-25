package com.circulation.random_complement.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.ArrayList;
import java.util.List;

public enum KeyBindings {
    RetrieveItem(new KeyBinding("key.retrieve_item.desc", KeyConflictContext.GUI, KeyModifier.CONTROL,-98,ClientProxy.categoryJEI)),
    StartCraft(new KeyBinding("key.start_craft.desc", KeyConflictContext.GUI, KeyModifier.ALT, -98, ClientProxy.categoryJEI));

    private final KeyBinding keyBinding;
    public static final List<KeyBinding> allKeyBinding = new ArrayList<>();

    KeyBindings(KeyBinding keyBinding) {
        this.keyBinding = keyBinding;
    }

    public KeyBinding getKeyBinding() {
        return this.keyBinding;
    }

    public static void init(){
        for (KeyBindings k : KeyBindings.values()) {
            ClientRegistry.registerKeyBinding(k.keyBinding);
            allKeyBinding.add(k.keyBinding);
        }
    }
}