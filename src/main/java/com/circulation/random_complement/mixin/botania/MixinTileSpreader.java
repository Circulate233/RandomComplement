package com.circulation.random_complement.mixin.botania;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.ITickable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.mana.BurstProperties;
import vazkii.botania.api.mana.IDirectioned;
import vazkii.botania.api.mana.IKeyLocked;
import vazkii.botania.api.mana.IManaCollector;
import vazkii.botania.api.mana.IManaSpreader;
import vazkii.botania.api.mana.IThrottledPacket;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.common.block.tile.TileSimpleInventory;
import vazkii.botania.common.block.tile.mana.TileSpreader;
import vazkii.botania.common.entity.EntityManaBurst;

@Mixin(value = TileSpreader.class, remap = false)
public abstract class MixinTileSpreader extends TileSimpleInventory implements IManaCollector, IWandBindable, IKeyLocked, IThrottledPacket, IManaSpreader, IDirectioned, ITickable {

    @Shadow
    public abstract int getMaxMana();

    @Inject(method = "getBurst", at = @At(value = "INVOKE", target = "Lvazkii/botania/common/entity/EntityManaBurst;setSourceLens(Lnet/minecraft/item/ItemStack;)V"))
    public void Fixtransfersettings(boolean fake, CallbackInfoReturnable<EntityManaBurst> cir, @Local(ordinal = 0) BurstProperties burstProperties) {
        if (burstProperties.maxMana > getMaxMana()) {
            burstProperties.maxMana = getMaxMana();
        }
    }
}
