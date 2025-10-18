package com.circulation.random_complement.mixin.thaumicenergistics;

import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumicenergistics.client.gui.helpers.MERepo;
import thaumicenergistics.client.gui.part.GuiAbstractTerminal;

@Mixin(value = GuiAbstractTerminal.class, remap = false)
public abstract class MixinGuiAbstractTerminal<T extends IAEStack<T>, C extends IStorageChannel<T>> extends MixinGuiBase {

    public MixinGuiAbstractTerminal(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Shadow
    public abstract MERepo<T> getRepo();

}
