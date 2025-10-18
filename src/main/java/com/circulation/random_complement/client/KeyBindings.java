package com.circulation.random_complement.client;

import com.circulation.random_complement.common.util.Functions;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public enum KeyBindings {
    RetrieveItem(new KeyBinding("key.retrieve_item.desc", KeyConflictContext.GUI, KeyModifier.CONTROL, -98, ClientProxy.categoryJEI), true),
    StartCraft(new KeyBinding("key.start_craft.desc", KeyConflictContext.GUI, KeyModifier.ALT, -98, ClientProxy.categoryJEI), true);

    @Getter
    private final KeyBinding keyBinding;
    @Getter
    private final boolean needItem;
    @Getter
    private final String tooltip;

    KeyBindings(KeyBinding keyBinding, boolean needItem) {
        this.keyBinding = keyBinding;
        this.needItem = needItem;
        this.tooltip = keyBinding.getKeyDescription() + ".tooltip";
    }

    public static KeyBindings getKeyFromID(int id) {
        return KeyBindings.values()[id];
    }

    public static List<String> getTooltipList() {
        val outs = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            val key = values()[i].keyBinding;
            outs[i] = I18n.format(values()[i].tooltip, key.getKeyModifier().getLocalizedComboName(key.getKeyCode()));
        }
        return Functions.asList(outs);
    }

    public static void init() {
        for (KeyBindings k : KeyBindings.values()) {
            ClientRegistry.registerKeyBinding(k.keyBinding);
        }
    }

    public int getID() {
        return this.ordinal();
    }
}