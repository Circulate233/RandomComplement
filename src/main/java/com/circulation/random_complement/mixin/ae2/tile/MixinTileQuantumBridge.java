package com.circulation.random_complement.mixin.ae2.tile;

import appeng.api.AEApi;
import appeng.api.definitions.IItemDefinition;
import appeng.me.cluster.implementations.QuantumCluster;
import appeng.tile.grid.AENetworkInvTile;
import appeng.tile.qnb.TileQuantumBridge;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import com.circulation.random_complement.common.interfaces.RCQuantumCluster;
import com.circulation.random_complement.mixin.ae2.AccessorAENetworkProxy;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileQuantumBridge.class, remap = false)
public abstract class MixinTileQuantumBridge extends AENetworkInvTile {

    @Unique
    private static IItemDefinition r$card;
    @Shadow
    private QuantumCluster cluster;

    @Inject(method = "isPowered", at = @At("HEAD"), cancellable = true)
    public void isPoweredI(CallbackInfoReturnable<Boolean> cir) {
        if (!Platform.isClient()) {
            if (this.cluster == null) {
                ((AccessorAENetworkProxy) this.getProxy()).r$setIdleDraw(0);
                cir.setReturnValue(true);
            } else if (((RCQuantumCluster) this.cluster).r$noWork()) {
                ((AccessorAENetworkProxy) this.getProxy()).r$setIdleDraw(0);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "onChangeInventory", at = @At("HEAD"))
    public void onChangeInventoryI(IItemHandler inv, int slot, InvOperation mc, ItemStack removed, ItemStack added, CallbackInfo ci) {
        if (r$card == null) {
            r$card = AEApi.instance().definitions().materials().cardQuantumLink();
        }
        if (this.getProxy().getIdlePowerUsage() == 0) {
            if (r$card.isSameAs(added)) {
                ((RCQuantumCluster) this.cluster).r$setIdlePowerUsage(this.cluster, 22);
            }
        } else if (r$card.isSameAs(removed)) {
            ((RCQuantumCluster) this.cluster).r$setIdlePowerUsage(this.cluster, 0);
        }
    }
}