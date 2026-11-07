package com.circulation.random_complement.client.handler;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

/** Tracks keys claimed by a shortcut until release, including changes to modifiers or hover. */
public final class ShortcutKeyState {

    private final Set<Integer> handledKeys = new HashSet<>();

    public void clear() {
        handledKeys.clear();
    }

    public boolean handle(int key, boolean down, boolean repeat, BooleanSupplier action) {
        if (key <= 0) return false;
        if (!down) return handledKeys.remove(key);
        if (handledKeys.contains(key)) return true;
        if (repeat || !action.getAsBoolean()) return false;
        handledKeys.add(key);
        return true;
    }
}
