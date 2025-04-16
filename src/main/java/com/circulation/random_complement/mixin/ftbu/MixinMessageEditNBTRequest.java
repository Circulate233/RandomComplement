package com.circulation.random_complement.mixin.ftbu;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import com.feed_the_beast.ftbutilities.net.MessageEditNBTRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MessageEditNBTRequest.class)
public abstract class MixinMessageEditNBTRequest extends MessageToClient {


    @SideOnly(Side.CLIENT)
    public void onMessage() {
        ftbeditNBT();
    }

    @Unique
    @SideOnly(Side.CLIENT)
    private static void ftbeditNBT() {
        RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;
        if (ray != null) {
            if (ray.typeOfHit == RayTraceResult.Type.BLOCK) {
                ClientUtils.execClientCommand(StringJoiner.with(' ').joinObjects("/ftbnbtedit block", ray.getBlockPos().getX(), ray.getBlockPos().getY(), ray.getBlockPos().getZ()));
            } else if (ray.typeOfHit == RayTraceResult.Type.ENTITY && ray.entityHit != null) {
                ClientUtils.execClientCommand("/ftbnbtedit entity " + ray.entityHit.getEntityId());
            }
        }
    }

}
