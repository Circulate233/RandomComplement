package com.circulation.random_complement.mixin.ae2;

import appeng.api.config.SecurityPermissions;
import appeng.me.cache.SecurityCache;
import com.circulation.random_complement.RCConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SecurityCache.class,remap = false)
public class MixinSecurityCache {

    @Inject(method = "hasPermission(ILappeng/api/config/SecurityPermissions;)Z", at = @At(value= "HEAD"), cancellable = true)
    public void hasPermissionMixin1(int playerID, SecurityPermissions perm, CallbackInfoReturnable<Boolean> cir) {
        if (RCConfig.AE2.SecurityCache && perm == SecurityPermissions.BUILD){
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

}
