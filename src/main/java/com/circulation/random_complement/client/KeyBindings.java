package com.circulation.random_complement.client;

import lombok.Getter;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public enum KeyBindings {
    RetrieveItem(new KeyBinding("key.retrieve_item.desc", KeyConflictContext.GUI, KeyModifier.CONTROL,-98,ClientProxy.categoryJEI),true),
    StartCraft(new KeyBinding("key.start_craft.desc", KeyConflictContext.GUI, KeyModifier.ALT, -98, ClientProxy.categoryJEI),true);

    @Getter
    private final KeyBinding keyBinding;
    private final boolean needItem;

    KeyBindings(KeyBinding keyBinding,boolean needItem) {
        this.keyBinding = keyBinding;
        this.needItem = needItem;
    }

    public static KeyBindings getKeyFromID(int id){
        return KeyBindings.values()[id];
    }

    public int getID(){
        return this.ordinal();
    }

    public boolean needItem() {
        return this.needItem;
    }

    public static void init(){
        for (KeyBindings k : KeyBindings.values()) {
            ClientRegistry.registerKeyBinding(k.keyBinding);
        }
    }
}