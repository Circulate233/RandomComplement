package com.circulation.random_complement.mixin.botania.flower;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import vazkii.botania.api.mana.IManaCollector;
import vazkii.botania.api.mana.ManaNetworkEvent;
import vazkii.botania.api.mana.TileSignature;
import vazkii.botania.common.core.handler.ManaNetworkHandler;

import java.util.Set;
import java.util.WeakHashMap;

@Mixin(value = ManaNetworkHandler.class,remap = false)
public class MixinManaNetworkHandler {

    @Final
    @Shadow
    private WeakHashMap<World, Set<TileSignature>> manaPools;
    @Final
    @Shadow
    private WeakHashMap<World, Set<TileSignature>> manaCollectors;

    @Unique
    @SubscribeEvent
    public void rc$onNetworkEvent(ManaNetworkEvent event) {
        var tile = event.tile;
        if (tile instanceof IManaCollector) {
            if (event.action == ManaNetworkEvent.Action.ADD) {
                this.rc$add(tile);
            } else {
                this.rc$remove(tile);
            }
        }
    }

    @Unique
    private void rc$remove(TileEntity tile) {
        World world = tile.getWorld();
        if (manaCollectors.containsKey(world)) {
            manaCollectors.get(world).remove(new TileSignature(tile, tile.getWorld().isRemote));
        }
    }

    @Unique
    private void rc$add(TileEntity tile) {
        World world = tile.getWorld();
        manaCollectors.putIfAbsent(world, new ObjectOpenHashSet<>());
        manaCollectors.get(world).add(new TileSignature(tile, tile.getWorld().isRemote));
    }

}
