package com.circulation.random_complement.mixin.ftbu;

import com.circulation.random_complement.RCConfig;
import com.feed_the_beast.ftbutilities.command.CmdEditNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = CmdEditNBT.class,remap = false)
public class MixinCmdEditNBT {

    @ModifyConstant(method = "<init>", constant = @Constant(stringValue = "nbtedit"))
    private static String setCmdNBT(String constant) {
        if (RCConfig.FTBU.ModifyCmdEditNBT) {
            return "ftbnbtedit";
        } else {
            return constant;
        }
    }


}
