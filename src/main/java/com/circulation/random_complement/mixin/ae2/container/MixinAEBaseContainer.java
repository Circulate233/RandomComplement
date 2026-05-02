package com.circulation.random_complement.mixin.ae2.container;

import appeng.container.AEBaseContainer;
import com.circulation.random_complement.common.interfaces.RCAEBaseContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = AEBaseContainer.class, remap = false)
public abstract class MixinAEBaseContainer extends Container implements RCAEBaseContainer {

    @Unique
    private Container rc$oldContainer;

    @Shadow
    public abstract InventoryPlayer getPlayerInv();

    @Unique
    @Override
    public void rc$setOldContainer(Container old) {
        rc$oldContainer = old;
    }

    @Unique
    @Override
    public Container rc$getOldContainer() {
        return rc$oldContainer;
    }
}
