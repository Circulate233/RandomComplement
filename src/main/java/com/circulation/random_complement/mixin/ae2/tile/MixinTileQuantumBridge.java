package com.circulation.random_complement.mixin.ae2.tile;

import appeng.me.cluster.implementations.QuantumCluster;
import appeng.tile.grid.AENetworkInvTile;
import appeng.tile.qnb.TileQuantumBridge;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import com.circulation.random_complement.common.interfaces.RCQuantumCluster;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileQuantumBridge.class,remap = false)
public abstract class MixinTileQuantumBridge extends AENetworkInvTile {

    @Shadow
    private QuantumCluster cluster;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        this.getProxy().setIdlePowerUsage(0);
    }

    @Inject(method = "isPowered", at = @At("HEAD"), cancellable = true)
    public void isPowered(CallbackInfoReturnable<Boolean> cir) {
        if (!Platform.isClient()) {
            if (this.cluster == null) {
                cir.setReturnValue(true);
            } else if (((RCQuantumCluster)this.cluster).r$noWork()){
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "onChangeInventory", at = @At("HEAD"))
    public void onChangeInventory(IItemHandler inv, int slot, InvOperation mc, ItemStack removed, ItemStack added, CallbackInfo ci) {
        if (slot == 1){
            ((RCQuantumCluster)this.cluster).r$setIdlePowerUsage(this.cluster,inv.getStackInSlot(slot).isEmpty() ? 0 : 22);
        }
    }
}