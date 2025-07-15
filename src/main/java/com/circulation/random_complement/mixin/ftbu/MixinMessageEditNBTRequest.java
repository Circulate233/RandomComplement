package com.circulation.random_complement.mixin.ftbu;

import com.circulation.random_complement.RCConfig;
import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import com.feed_the_beast.ftbutilities.net.MessageEditNBTRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MessageEditNBTRequest.class, remap = false)
public abstract class MixinMessageEditNBTRequest {

    @Inject(method = "editNBT",at = @At("HEAD"), cancellable = true)
    private static void ftbeditNBT(CallbackInfo ci) {
        if (!RCConfig.FTBU.ModifyCmdEditNBT) {
            return;
        }
        RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;
        if (ray != null) {
            if (ray.typeOfHit == RayTraceResult.Type.BLOCK) {
                ClientUtils.execClientCommand(StringJoiner.with(' ').joinObjects("/ftbnbtedit block", ray.getBlockPos().getX(), ray.getBlockPos().getY(), ray.getBlockPos().getZ()));
            } else if (ray.typeOfHit == RayTraceResult.Type.ENTITY && ray.entityHit != null) {
                ClientUtils.execClientCommand("/ftbnbtedit entity " + ray.entityHit.getEntityId());
            }
        }
        ci.cancel();
    }

}
